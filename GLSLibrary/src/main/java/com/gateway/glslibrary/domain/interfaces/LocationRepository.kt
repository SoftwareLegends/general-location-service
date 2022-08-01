package com.gateway.glslibrary.domain.interfaces

import android.location.Location
import com.gateway.glslibrary.domain.Resource
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun lastLocation() : Flow<Resource<Location>>
    fun getCurrentLocation() : Flow<Resource<Location>>
    val isLocationServicesAvailable: Boolean
}