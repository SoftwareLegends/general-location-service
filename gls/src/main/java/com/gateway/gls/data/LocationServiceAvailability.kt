package com.gateway.gls.data

import com.gateway.gls.di.LibraryModule.application
import com.gateway.gls.di.LocationServiceModule
import com.gateway.gls.domain.Services
import com.google.android.gms.common.ConnectionResult
import timber.log.Timber

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

    private fun setServiceProvider(service: Services) = true.also {
        Timber.d(service::class.java.name.substringAfter('$'))
        serviceProvider = service
    }
}