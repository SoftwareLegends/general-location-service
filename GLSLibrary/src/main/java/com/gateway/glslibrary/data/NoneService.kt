package com.gateway.glslibrary.data

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.glslibrary.domain.Resource
import com.gateway.glslibrary.domain.interfaces.LocationService
import com.gateway.glslibrary.utils.enums.LocationFailure
import com.gateway.glslibrary.utils.extenstions.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoneService : LocationService {
    override suspend fun lastLocation(): Resource<Location> =
        Resource.Fail(data = LocationFailure.LOCATION_SERVICE_NOT_FOUND.toModel())

    override fun requestLocationUpdates(): Flow<Resource<Location>> =
        flow { emit(Resource.Fail(data = LocationFailure.LOCATION_SERVICE_NOT_FOUND.toModel())) }

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {}
}