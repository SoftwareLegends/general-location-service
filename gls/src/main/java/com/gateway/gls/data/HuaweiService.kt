package com.gateway.gls.data

import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.domain.models.ServiceFailure
import com.gateway.gls.utils.extenstions.await
import com.gateway.gls.utils.extenstions.isGpsProviderEnabled
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber

class HuaweiService(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationRequest: LocationRequest
) : LocationService {
    override suspend fun lastLocation(): Resource<Location> = safeCall {
        val location = fusedLocationClient.lastLocation.await()
        getLocationResult(context = context, location = location)
    }

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
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
    }

    override suspend fun requestLocationUpdates(): Resource<List<Location>> {
        val results: MutableList<Location> = mutableListOf()
        var status: Resource<List<Location>> = Resource.Init

        var isRunning: Boolean = true
        var numUpdates: Int = locationRequest.numUpdates

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                runCatching{
                    results.addAll(result.locations)
                    status = Resource.Success(data = results)
                }.onFailure {
                    status = Resource.Fail(
                        error = ServiceFailure.UnknownError(
                            message = it.message
                        )
                    )
                }

                numUpdates--

                if (numUpdates == 0)
                    isRunning = false
            }
        }

        if (context.isGpsProviderEnabled().not())
            status = Resource.Fail(error = ServiceFailure.GpsProviderIsDisabled())
        else
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        while (isRunning) {
            delay(100)
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        return status
    }

    override fun configureLocationRequest(
        priority: Int,
        interval: Long,
        fastestInterval: Long,
        numUpdates: Int
    ) {
        locationRequest.apply {
            this.priority = priority
            this.interval = interval
            this.fastestInterval = fastestInterval
            this.numUpdates = numUpdates
        }
    }

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {
        // Define a device setting client.

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        // Create a settingsClient object.
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnFailureListener { exception ->
            Timber.d("Location settings request failed")
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().

                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution)
                        .build()
                    resultContracts.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Timber.d(sendEx.message)
                }
            }
        }
    }
}
