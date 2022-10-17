package com.gateway.gls.data

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.domain.interfaces.LocationService
import com.gateway.gls.domain.models.ServiceFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoneService : LocationService {
    override suspend fun lastLocation(): Resource<Location> =
        Resource.Fail(error = ServiceFailure.LocationServiceNotFound())

    override suspend fun requestLocationUpdates(): Resource<List<Location>> =
        Resource.Fail(error = ServiceFailure.LocationServiceNotFound())

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> =
        flow { emit(Resource.Fail(error = ServiceFailure.LocationServiceNotFound())) }

    override fun configureLocationRequest(
        priority: Int,
        interval: Long,
        fastestInterval: Long,
        numUpdates: Int
    ) {}

    override fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {}
}
