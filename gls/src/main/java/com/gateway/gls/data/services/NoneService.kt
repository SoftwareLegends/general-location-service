package com.gateway.gls.data.services

import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.gateway.core.base.Resource
import com.gateway.gls.domain.base.LocationService
import com.gateway.gls.domain.entities.ServiceFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class NoneService : LocationService {
    override suspend fun getLastLocation(): Resource<Location> =
        Resource.Fail(error = ServiceFailure.LocationServiceNotFound())

    override suspend fun requestLocationUpdates(): Resource<List<Location>> =
        Resource.Fail(error = ServiceFailure.LocationServiceNotFound())

    override fun requestLocationUpdatesAsFlow(): Flow<Resource<Location>> =
        flow { emit(Resource.Fail(error = ServiceFailure.LocationServiceNotFound())) }

    override fun configureLocationRequest(
        priority: Int,
        intervalMillis: Long,
        minUpdateIntervalMillis: Long,
        maxUpdates: Int,
        maxUpdateDelayMillis: Long
    ) {}

    override fun requestLocationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>) {}
}
