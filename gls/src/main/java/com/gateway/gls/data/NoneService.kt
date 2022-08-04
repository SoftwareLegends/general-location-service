package com.gateway.gls.data

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.utils.enums.LocationFailure
import com.gateway.gls.utils.extenstions.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoneService : LocationService {
    override suspend fun lastLocation(): Resource<Location> =
        Resource.Fail(error = LocationFailure.LOCATION_SERVICE_NOT_FOUND.toModel())

    override fun requestLocationUpdates(): Flow<Resource<Location>> =
        flow { emit(Resource.Fail(error = LocationFailure.LOCATION_SERVICE_NOT_FOUND.toModel())) }

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {}
}