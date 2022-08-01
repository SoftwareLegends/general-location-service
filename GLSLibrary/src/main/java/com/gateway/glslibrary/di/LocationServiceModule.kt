package com.gateway.glslibrary.di

import com.gateway.glslibrary.di.LibraryModule.application
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.google.android.gms.location.LocationServices as GoogleLocationServices
import com.huawei.hms.location.LocationServices as HuaweiLocationServices

object LocationServiceModule {
    val googleLocationProviderClient by lazy { provideGoogleLocationProviderClient() }
    val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    val googleApiAvailability: GoogleApiAvailability by lazy { provideGoogleApiAvailability() }
    val huaweiApiAvailability: HuaweiApiAvailability by lazy { provideHuaweiApiAvailability() }

    private fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    private fun provideHuaweiApiAvailability() = HuaweiApiAvailability.getInstance()

    private fun provideGoogleLocationProviderClient() =
        GoogleLocationServices.getFusedLocationProviderClient(application)

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(application)
}