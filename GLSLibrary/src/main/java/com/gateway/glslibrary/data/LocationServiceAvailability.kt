package com.gateway.glslibrary.data

import com.gateway.glslibrary.di.LibraryModule.application
import com.gateway.glslibrary.di.LocationServiceModule
import com.gateway.glslibrary.domain.Services
import com.google.android.gms.common.ConnectionResult

object LocationServiceAvailability {
    var serviceProvider: Services = Services.None

    fun isLocationServicesAvailable() = with(LocationServiceModule) {
        when (ConnectionResult.SUCCESS) {
            huaweiApiAvailability.isHuaweiMobileServicesAvailable(application) -> setServiceProvider(
                service = Services.Huawei
            )
            else -> false
        }
    }

    private fun setServiceProvider(service: Services) = true.also { serviceProvider = service }
}