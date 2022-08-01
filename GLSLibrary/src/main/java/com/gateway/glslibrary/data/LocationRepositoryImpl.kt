package com.gateway.glslibrary.data

import android.location.Location
import com.gateway.glslibrary.domain.interfaces.LocationRepository
import com.gateway.glslibrary.domain.Resource
import kotlinx.coroutines.flow.Flow

class LocationRepositoryImpl : LocationRepository {
    override fun lastLocation(): Flow<Resource<Location>> {
        TODO("Not yet implemented")
    }

    override fun getCurrentLocation(): Flow<Resource<Location>> {
        TODO("Not yet implemented")
    }
}
