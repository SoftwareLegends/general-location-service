package com.gateway.glslibrary.data

import com.gateway.glslibrary.di.LibraryModule.application
import com.gateway.glslibrary.di.LocationServiceModule
import com.gateway.glslibrary.domain.Services
import com.google.android.gms.common.ConnectionResult

object LocationServiceAvailability {
    var serviceProvider: Services = Services.None
    var isLocationServicesAvailable: Boolean = false

    fun initializeService() {
        isLocationServicesAvailable = with(LocationServiceModule) {
            when (ConnectionResult.SUCCESS) {
                googleApiAvailability.isGooglePlayServicesAvailable(application) -> setServiceProvider(
                    service = Services.Google
                )
                huaweiApiAvailability.isHuaweiMobileServicesAvailable(application) -> setServiceProvider(
                    service = Services.Huawei
                )
                else -> false
            }
        }
    }

    private fun setServiceProvider(service: Services) = true.also { serviceProvider = service }
}