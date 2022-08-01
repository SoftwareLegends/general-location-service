package com.gateway.glslibrary.di

import com.gateway.glslibrary.di.LibraryModule.application
import com.huawei.hms.api.HuaweiApiAvailability
import com.huawei.hms.location.LocationServices as HuaweiLocationServices

object LocationServiceModule {
    val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    val huaweiApiAvailability: HuaweiApiAvailability by lazy { provideHuaweiApiAvailability() }

    private fun provideHuaweiApiAvailability() = HuaweiApiAvailability.getInstance()

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(application)
}