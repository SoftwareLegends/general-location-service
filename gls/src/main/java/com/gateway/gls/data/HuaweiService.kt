package com.gateway.gls.data

import android.content.Context
import android.content.IntentSender
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.domain.models.ServiceFailure
import com.gateway.gls.utils.extenstions.await
import com.huawei.hms.common.ResolvableApiException
import com.huawei.hms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
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

    override fun requestLocationUpdates(): Flow<Resource<Location>> = callbackFlow {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    trySendBlocking(Resource.Success(data = location))
                        .onFailure {
                            trySendBlocking(Resource.Fail(error = ServiceFailure.UnknownError(message = it?.message)))
                        }
                }
            }
        }
        awaitClose { fusedLocationClient.removeLocationUpdates(locationCallback) }
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
                }
            }
        }
    }
}