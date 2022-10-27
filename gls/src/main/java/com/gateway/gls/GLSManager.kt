package com.gateway.gls

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.altaie.prettycode.core.base.Resource
import com.gateway.gls.domain.base.LocationRepository
import com.gateway.gls.domain.entities.Priority
import com.gateway.gls.domain.entities.Services
import kotlinx.coroutines.flow.Flow

class GLSManager(
    private val repository: LocationRepository,
    val serviceProvider: Services,
    val isServicesAvailable: Boolean
    ) : LocationRepository {
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
}
