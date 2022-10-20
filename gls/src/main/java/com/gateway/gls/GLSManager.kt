package com.gateway.gls

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.core.base.Resource
import com.gateway.gls.di.GLSModule.repository
import com.gateway.gls.domain.base.LocationRepository
import com.gateway.gls.domain.entities.Priority
import com.gateway.gls.domain.entities.Services
import com.gateway.gls.data.services.ServiceAvailability
import kotlinx.coroutines.flow.Flow

class GLSManager : LocationRepository {
    override fun lastLocationAsFlow(): Flow<Resource<Location>> =
        repository.lastLocationAsFlow()

    override suspend fun lastLocation(): Resource<Location> =
        repository.lastLocation()

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> =
        repository.requestLocationUpdatesAsFlow()

    override suspend fun requestLocationUpdates(): Resource<List<Location>> =
        repository.requestLocationUpdates()

    override fun configureLocationRequest(
        priority: Priority,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long
    ) = repository.configureLocationRequest(
        priority = priority,
        intervalMillis = intervalMillis,
        minUpdateIntervalMillis = minUpdateIntervalMillis,
        maxUpdates = maxUpdates,
        maxUpdateDelayMillis = maxUpdateDelayMillis
    )

    override fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) =
        repository.requestLocationSettings(resultContracts)

    override val isLocationServicesAvailable: Boolean
        get() = repository.isLocationServicesAvailable

    val serviceProvider: Services
        get() = ServiceAvailability.serviceProvider
}
