package com.gateway.gls.domain.base

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.altaie.prettycode.core.base.Resource
import com.gateway.gls.domain.entities.Priority
import com.gateway.gls.utils.LocationRequestDefaults
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun lastLocationAsFlow(): Flow<Resource<Location>>
    suspend fun lastLocation(): Resource<Location>

    fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>>

    suspend fun requestLocationUpdates(): Resource<List<Location>>

    fun removeLocationUpdates()

    fun configureLocationRequest(
        priority: Priority = Priority.HighAccuracy,
        intervalMillis: Long = LocationRequestDefaults.UPDATE_INTERVAL_MILLIS,
        minUpdateIntervalMillis: Long = LocationRequestDefaults.MIN_UPDATE_INTERVAL_MILLIS,
        maxUpdates: Int = LocationRequestDefaults.MAX_UPDATES,
        maxUpdateDelayMillis: Long = LocationRequestDefaults.MAX_UPDATE_DELAY_MILLIS,
    )

    fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>)
}
