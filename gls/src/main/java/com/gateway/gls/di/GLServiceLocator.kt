package com.gateway.gls.di

import android.content.Context
import com.gateway.gls.data.*
import com.gateway.gls.domain.interfaces.LocationRepository
import com.gateway.gls.domain.models.Services
import com.gateway.gls.utils.LocationRequestProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import timber.log.Timber
import com.google.android.gms.location.LocationServices as GoogleLocationServices
import com.huawei.hms.location.LocationServices as HuaweiLocationServices

object GLServiceLocator {
    @Volatile
    private lateinit var applicationContext: Context
    private val googleLocationProviderClient by lazy { provideGoogleLocationProviderClient() }
    private val huaweiLocationProviderClient by lazy { provideHuaweiLocationProviderClient() }
    private val googleApiAvailability: GoogleApiAvailability by lazy { provideGoogleApiAvailability() }
    private val huaweiApiAvailability: HuaweiApiAvailability by lazy { provideHuaweiApiAvailability() }
    val locationRepository: LocationRepository by lazy { provideLocationRepository() }
    private val googleService: GoogleService by lazy { provideGoogleService() }
    private val huaweiService: HuaweiService by lazy { provideHuaweiService() }

    /**
     * This function should called in MainActivity or an Application instance.

     * to provide an applicationContext instance for the service.
     *
     * @author Ahmed Mones
     */
    fun initializeService(applicationContext: Context) {
        if (this::applicationContext.isInitialized.not()) {
            synchronized(applicationContext) { this.applicationContext = applicationContext }
            prepareAvailability()
        }
    }

    private fun provideGoogleService() = GoogleService(
        context = applicationContext,
        locationRequest = LocationRequestProvider.Google.locationRequest,
        fusedLocationClient = googleLocationProviderClient
    )

    private fun provideHuaweiService() = HuaweiService(
        context = applicationContext,
        locationRequest = LocationRequestProvider.Huawei.locationRequest,
        fusedLocationClient = huaweiLocationProviderClient
    )

    private fun provideGoogleApiAvailability() = GoogleApiAvailability.getInstance()

    private fun provideHuaweiApiAvailability() = HuaweiApiAvailability.getInstance()

    private fun provideGoogleLocationProviderClient() =
        GoogleLocationServices.getFusedLocationProviderClient(applicationContext)

    private fun provideHuaweiLocationProviderClient() =
        HuaweiLocationServices.getFusedLocationProviderClient(applicationContext)

    private fun provideLocationRepository(): LocationRepository = LocationRepositoryImpl(
        service = when (GLServiceAvailability.serviceProvider) {
            is Services.Google -> googleService
            is Services.Huawei -> huaweiService
            else -> NoneService()
        }
    )

    private fun prepareAvailability() {
        GLServiceAvailability.isServicesAvailable = with(GLServiceLocator) {
            when (ConnectionResult.SUCCESS) {
                googleApiAvailability.isGooglePlayServicesAvailable(applicationContext) -> setServiceProvider(
                    service = Services.Google
                )
                huaweiApiAvailability.isHuaweiMobileServicesAvailable(applicationContext) -> setServiceProvider(
                    service = Services.Huawei
                )
                else -> false
            }
        }
    }

    private fun setServiceProvider(service: Services) = true.also {
        Timber.d(service::class.java.name.substringAfter('$'))
        GLServiceAvailability.serviceProvider = service
    }
}
