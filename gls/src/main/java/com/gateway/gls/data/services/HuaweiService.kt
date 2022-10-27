package com.gateway.gls.data.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import android.os.Looper
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.altaie.prettycode.core.base.Resource
import com.gateway.gls.data.LocationRequestProvider
import com.gateway.gls.domain.entities.ServiceFailure
import com.gateway.gls.domain.base.LocationService
import com.gateway.gls.utils.LocationRequestDefaults
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

@SuppressLint("MissingPermission")
internal class HuaweiService(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private var locationRequest: LocationRequest
) : LocationService {
    override suspend fun getLastLocation(): Resource<Location> = safeCall {
        val location = fusedLocationClient.lastLocation.await()
        getLocationResult(context = context, location = location)
    }

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> = callbackFlow {
        trySend(Resource.Loading)

        val locationCallback = object : LocationCallback() {
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
        var currentUpdate: Int = numUpdates
        var safeCounter = LocationRequestDefaults.SAFE_COUNTER


        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.locations.minBy { it.accuracy }
                results.add(location)
                status = Resource.Success(data = results)

                numUpdates--

                if (numUpdates == 0)
                    isRunning = false

                currentUpdate = numUpdates
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
            delay(locationRequest.run { interval + maxWaitTime })

            if (currentUpdate == numUpdates)
                safeCounter--
            else
                safeCounter = LocationRequestDefaults.SAFE_COUNTER
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        return status
    }

    override fun configureLocationRequest(
        priority: Int,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long
    ) {
        locationRequest = LocationRequestProvider.Huawei(
            priority = priority,
            maxUpdates = maxUpdates,
            intervalMillis = intervalMillis,
            maxUpdateDelayMillis = maxUpdateDelayMillis,
            minUpdateIntervalMillis = minUpdateIntervalMillis,
        ).locationRequest
    }

    override fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {
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
