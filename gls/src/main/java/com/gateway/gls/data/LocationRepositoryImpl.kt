package com.gateway.gls.data

import android.annotation.SuppressLint
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.interfaces.LocationRepository
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.domain.entities.Priority
import com.gateway.core.base.Resource
import com.gateway.gls.services.ServiceAvailability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@SuppressLint("MissingPermission")
internal class LocationRepositoryImpl(private val service: LocationService) : LocationRepository {
    override fun lastLocationAsFlow(): Flow<Resource<Location>> = wrapWithFlow(service::getLastLocation)

    override suspend fun lastLocation(): Resource<Location> = service.getLastLocation()

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> =
        service.requestLocationUpdatesAsFlow()

    override suspend fun requestLocationUpdates(): Resource<List<Location>> =
        service.requestLocationUpdates()

    override fun configureLocationRequest(
        priority: Priority,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long
    ) {
        service.configureLocationRequest(
            priority = priority.value,
            intervalMillis = intervalMillis,
            maxUpdates = maxUpdates,
            minUpdateIntervalMillis = minUpdateIntervalMillis,
            maxUpdateDelayMillis = maxUpdateDelayMillis
        )
    }

    override fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) =
        service.requestLocationSettings(resultContracts = resultContracts)

    override val isLocationServicesAvailable: Boolean = ServiceAvailability.isServicesAvailable

    private fun <T> wrapWithFlow(block: suspend () -> Resource<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading)
        emit(block())
    }
}
