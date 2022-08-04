package com.gateway.gls.domain.interfaces

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.Resource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun lastLocation() : Flow<Resource<Location>>
    fun requestLocationUpdates() : Flow<Resource<Location>>
    fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>)
    val isLocationServicesAvailable: Boolean
}