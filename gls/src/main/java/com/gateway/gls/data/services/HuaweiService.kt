package com.gateway.gls.data.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.altaie.prettycode.core.base.Resource
import com.altaie.prettycode.core.mapper.toUnKnownError
import com.gateway.gls.data.LocationRequestProvider
import com.gateway.gls.domain.base.LocationService
import com.gateway.gls.domain.entities.ServiceFailure
import com.gateway.gls.utils.extenstions.await
import com.gateway.gls.utils.extenstions.isEqual
import com.gateway.gls.utils.extenstions.isGpsProviderEnabled
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.time.Duration

@SuppressLint("MissingPermission")
internal class HuaweiService(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private var locationRequest: LocationRequest
) : LocationService {
    private lateinit var locationCallback: LocationCallback

    override suspend fun getLastLocation(): Resource<Location> = safeCall {
        val location = fusedLocationClient.lastLocation.await()
        getLocationResult(context = context, location = location)
    }

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> = callbackFlow {
        trySend(Resource.Loading)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.locations.minBy { it.accuracy }

                trySendBlocking(Resource.Success(data = location))
                    .onFailure {
                        trySendBlocking(
                            Resource.Fail(
                                error = ServiceFailure.UnknownError(
                                    message = it?.message
                                )
                            )
                        )
                    }
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                if (availability.isLocationAvailable.not()) {
                    fusedLocationClient.removeLocationUpdates(this)
                    trySend(Resource.Fail(error = ServiceFailure.LocationServiceNotFound()))
                }
            }
        }

        if (context.isGpsProviderEnabled().not())
            trySend(Resource.Fail(error = ServiceFailure.GpsProviderIsDisabled()))
        else
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
    }.distinctUntilChanged { old, new ->
        old.toData.isEqual(new.toData)
    }.buffer(Channel.UNLIMITED)

    override suspend fun requestLocationUpdates(timeout: Duration): Resource<List<Location>> =
        withTimeout(timeout) {
            suspendCancellableCoroutine { continuation ->
                val locations: MutableList<Location> = mutableListOf()

                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        val location = result.locations.minBy { it.accuracy }
                        locations.add(location)
                        if (locations.size >= locationRequest.numUpdates) {
                            fusedLocationClient.removeLocationUpdates(this)
                            continuation.resume(Resource.Success(data = locations))
                        }
                    }

                    override fun onLocationAvailability(availability: LocationAvailability) {
                        if (availability.isLocationAvailable.not()) {
                            fusedLocationClient.removeLocationUpdates(this)
                            continuation.resume(Resource.Fail(error = ServiceFailure.LocationServiceNotFound()))
                        }
                    }
                }

                runCatching {
                    if (context.isGpsProviderEnabled().not())
                        continuation.resume(Resource.Fail(error = ServiceFailure.GpsProviderIsDisabled()))
                    else
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                }.onFailure { continuation.resume(Resource.Fail(error = it.toUnKnownError())) }

                continuation.invokeOnCancellation {
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
            }
        }

    override fun removeLocationUpdates() {
        if (::locationCallback.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun configureLocationRequest(
        priority: Int,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long,
        minUpdateDistanceMeters: Float,
    ) {
        locationRequest = LocationRequestProvider.Huawei(
            priority = priority,
            maxUpdates = maxUpdates,
            intervalMillis = intervalMillis,
            maxUpdateDelayMillis = maxUpdateDelayMillis,
            minUpdateIntervalMillis = minUpdateIntervalMillis,
            minUpdateDistanceMeters = minUpdateDistanceMeters
        ).locationRequest
    }

    override fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        LocationServices.getSettingsClient(context)
            .checkLocationSettings(builder.build())
            .addOnFailureListener { exception ->
                handleLocationSettingsFailure(exception, resultContracts)
            }
    }

    private fun handleLocationSettingsFailure(
        exception: Exception,
        resultContracts: ActivityResultLauncher<IntentSenderRequest>
    ) {
        if (exception is ResolvableApiException)
            runCatching {
                val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution)
                    .build()
                resultContracts.launch(intentSenderRequest)
            }.onFailure {
                Timber.d("Failed to launch intent sender: ${it.message}")
            }
        else
            Timber.d("Location settings request failed with unknown exception: ${exception.message}")
    }
}
