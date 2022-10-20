package com.gateway.gls.di

import com.gateway.gls.services.ServiceAvailability
import com.gateway.gls.data.LocationRepositoryImpl
import com.gateway.gls.di.GLSInitializer.applicationContext
import com.gateway.gls.domain.interfaces.LocationRepository
import com.gateway.gls.domain.entities.Services
import com.gateway.gls.services.GoogleService
import com.gateway.gls.services.HuaweiService
import com.gateway.gls.services.NoneService
import com.gateway.gls.data.LocationRequestProvider
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices as GoogleLocationServices
import com.huawei.hms.location.LocationServices as HuaweiLocationServices
import com.huawei.hms.api.HuaweiApiAvailability

internal object GLSModule {
    private val googleService: GoogleService by lazy { provideGoogleService() }
    private val huaweiService: HuaweiService by lazy { provideHuaweiService() }
    private val googleLocationProviderClient by lazy { provideGoogleLocationProviderClient() }
    private val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    val googleApiAvailability: GoogleApiAvailability by lazy { GoogleApiAvailability.getInstance() }
    val huaweiApiAvailability: HuaweiApiAvailability by lazy { HuaweiApiAvailability.getInstance() }
    val repository: LocationRepository by lazy { provideLocationRepository() }

    private fun provideGoogleLocationProviderClient() =
        GoogleLocationServices.getFusedLocationProviderClient(applicationContext)

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(applicationContext)

    private fun provideGoogleService() = GoogleService(
        context = applicationContext,
        locationRequest = LocationRequestProvider.Google().locationRequest,
        fusedLocationClient = googleLocationProviderClient
    )

    private fun provideHuaweiService() = HuaweiService(
        context = applicationContext,
        locationRequest = LocationRequestProvider.Huawei().locationRequest,
        fusedLocationClient = huaweiLocationProviderClient
    )

    private fun provideLocationRepository(): LocationRepository = LocationRepositoryImpl(
        service = when (ServiceAvailability.serviceProvider) {
            is Services.Google -> googleService
            is Services.Huawei -> huaweiService
            else -> NoneService()
        }
    )
}
