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
import com.gateway.gls.domain.base.LocationService
import com.gateway.gls.domain.entities.ServiceFailure
import com.gateway.gls.utils.LocationRequestDefaults
import com.gateway.gls.utils.extenstions.isGpsProviderEnabled
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@SuppressLint("MissingPermission")
internal class GoogleService(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    private var locationRequest: LocationRequest
) : LocationService {
    private lateinit var locationCallback: LocationCallback

    override suspend fun getLastLocation(): Resource<Location> = safeCall {
        val location = fusedLocationClient.lastLocation.await()
        getLocationResult(context = context, location = location)
    }

    override fun configureLocationRequest(
        priority: Int,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long,
        minDistanceThreshold: Float,
    ) {
        locationRequest = LocationRequestProvider.Google(
            priority = priority,
            maxUpdates = maxUpdates,
            intervalMillis = intervalMillis,
            maxUpdateDelayMillis = maxUpdateDelayMillis,
            minUpdateIntervalMillis = minUpdateIntervalMillis,
            minDistanceThreshold = minDistanceThreshold
        ).locationRequest
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

                fusedLocationClient.flushLocations()
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
    }.distinctUntilChanged()
        .buffer(Channel.UNLIMITED)

    override suspend fun requestLocationUpdates(): Resource<List<Location>> {
        val results: MutableList<Location> = mutableListOf()
        var status: Resource<List<Location>> = Resource.Init

        var isRunning: Boolean = true
        var numUpdates: Int = locationRequest.numUpdates
        var currentUpdate: Int = numUpdates
        var safeCounter = LocationRequestDefaults.SAFE_COUNTER

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.locations.minBy { it.accuracy }
                results.add(location)
                status = Resource.Success(data = results)
                Timber.d(location.toString())
                numUpdates--

                if (numUpdates == 0)
                    isRunning = false

                currentUpdate = numUpdates
            }
        }

        if (context.isGpsProviderEnabled().not())
            return Resource.Fail(error = ServiceFailure.GpsProviderIsDisabled())
        else
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        while (isRunning && safeCounter != 0) {
            delay(locationRequest.run { interval + maxWaitTime })

            if (currentUpdate == numUpdates)
                safeCounter--
            else
                safeCounter = LocationRequestDefaults.SAFE_COUNTER
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        return status
    }

    override fun removeLocationUpdates() {
        if (::locationCallback.isInitialized)
            fusedLocationClient.removeLocationUpdates(locationCallback)
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
