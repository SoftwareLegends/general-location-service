package com.gateway.gls.di

import android.content.Context
import com.gateway.gls.data.services.GoogleService
import com.gateway.gls.data.services.HuaweiService
import com.gateway.gls.data.LocationRequestProvider
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices as GoogleLocationServices
import com.huawei.hms.location.LocationServices as HuaweiLocationServices
import com.huawei.hms.api.HuaweiApiAvailability

internal class GLSModule(private val context: Context) {
    val googleService: GoogleService by lazy { provideGoogleService() }
    val huaweiService: HuaweiService by lazy { provideHuaweiService() }
    private val googleLocationProviderClient by lazy { provideGoogleLocationProviderClient() }
    private val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    val googleApiAvailability: GoogleApiAvailability by lazy { GoogleApiAvailability.getInstance() }
    val huaweiApiAvailability: HuaweiApiAvailability by lazy { HuaweiApiAvailability.getInstance() }

    private fun provideGoogleLocationProviderClient() =
        GoogleLocationServices.getFusedLocationProviderClient(context)

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(context)

    private fun provideGoogleService() = GoogleService(
        context = context,
        locationRequest = LocationRequestProvider.Google().locationRequest,
        fusedLocationClient = googleLocationProviderClient
    )

    private fun provideHuaweiService() = HuaweiService(
        context = context,
        locationRequest = LocationRequestProvider.Huawei().locationRequest,
        fusedLocationClient = huaweiLocationProviderClient
    )


}
