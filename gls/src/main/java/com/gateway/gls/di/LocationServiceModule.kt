package com.gateway.gls.di

import com.gateway.gls.data.*
import com.gateway.gls.di.LibraryModule.application
import com.gateway.gls.domain.Services
import com.gateway.gls.domain.interfaces.LocationRepository
import com.gateway.gls.utils.LocationRequestProvider
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.google.android.gms.location.LocationServices as GoogleLocationServices
import com.huawei.hms.location.LocationServices as HuaweiLocationServices

object LocationServiceModule {
    private val googleLocationProviderClient by lazy { provideGoogleLocationProviderClient() }
    private val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    val googleApiAvailability: GoogleApiAvailability by lazy { provideGoogleApiAvailability() }
    val huaweiApiAvailability: HuaweiApiAvailability by lazy { provideHuaweiApiAvailability() }
    val locationRepository: LocationRepository by lazy { provideLocationRepository() }
    private val googleService: GoogleService by lazy { provideGoogleService() }
    private val huaweiService: HuaweiService by lazy { provideHuaweiService() }

    private fun provideGoogleService() = GoogleService(
        context = application,
        locationRequest = LocationRequestProvider.Google.locationRequest,
        fusedLocationClient = googleLocationProviderClient
    )

    private fun provideHuaweiService() = HuaweiService(
        context = application,
        locationRequest = LocationRequestProvider.Huawei.locationRequest,
        fusedLocationClient = huaweiLocationProviderClient
    )

    private fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    private fun provideHuaweiApiAvailability() = HuaweiApiAvailability.getInstance()

    private fun provideGoogleLocationProviderClient() =
        GoogleLocationServices.getFusedLocationProviderClient(application)

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(application)

    private fun provideLocationRepository() : LocationRepository = LocationRepositoryImpl(
        service = when (LocationServiceAvailability.serviceProvider) {
            is Services.Google -> googleService
            is Services.Huawei -> huaweiService
            else -> NoneService()
        }
    )
}