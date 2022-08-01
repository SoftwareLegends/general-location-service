package com.gateway.glslibrary.data

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.glslibrary.domain.Resource
import com.gateway.glslibrary.domain.interfaces.LocationService

class NoneService: LocationService {
    override suspend fun lastLocation(): Resource<Location> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentLocation(): Resource<Location> {
        TODO("Not yet implemented")
    }

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {
        TODO("Not yet implemented")
    }
}