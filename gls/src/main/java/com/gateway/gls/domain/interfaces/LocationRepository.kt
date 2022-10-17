package com.gateway.gls.domain.interfaces

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.models.Priority
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.utils.Constant
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun lastLocationAsFlow(): Flow<Resource<Location>>
    suspend fun lastLocation(): Resource<Location>

    fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>>

    suspend fun requestLocationUpdates(): Resource<List<Location>>

    fun configureLocationRequest(
        priority: Priority = Priority.HighAccuracy,
        interval: Long = Constant.UPDATE_INTERVAL_IN_MILLISECONDS,
        fastestInterval: Long = Constant.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS,
        numUpdates: Int = Constant.NUMBER_OF_UPDATES
    )

    fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>)

    val isLocationServicesAvailable: Boolean
}
