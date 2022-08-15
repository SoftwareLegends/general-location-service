package com.gateway.gls.data

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.interfaces.LocationRepository
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.domain.models.Priority
import com.gateway.gls.domain.models.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LocationRepositoryImpl(private val service: LocationService) : LocationRepository {
    override fun lastLocation(): Flow<Resource<Location>> = wrapWithFlow(service::lastLocation)
    override fun requestLocationUpdates(): Flow<Resource<Location>> =
        service.requestLocationUpdates()

    override fun configureLocationRequest(
        priority: Priority,
        interval: Long,
        fastestInterval: Long,
        numUpdates: Int
    ) {
        service.configureLocationRequest(
            priority = priority.value,
            interval = interval,
            numUpdates = numUpdates,
            fastestInterval = fastestInterval
        )
    }

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) =
        service.locationSettings(resultContracts = resultContracts)

    override val isLocationServicesAvailable: Boolean = GLServiceAvailability.isServicesAvailable

    private fun <T> wrapWithFlow(block: suspend () -> Resource<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        emit(block())
    }
}