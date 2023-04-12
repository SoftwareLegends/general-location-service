package com.gateway.gls.di

import android.content.Context
import com.gateway.gls.GLSManager
import com.gateway.gls.data.LocationRepositoryImpl
import com.gateway.gls.data.services.NoneService
import com.gateway.gls.domain.base.LocationRepository
import com.gateway.gls.domain.entities.Services
import com.google.android.gms.common.ConnectionResult
import timber.log.Timber

class GLSInitializer(private val applicationContext: Context) {
    private var serviceProvider: Services = Services.None
    private var isServicesAvailable: Boolean = false
    private var module: GLSModule

    init { module = GLSModule(context = applicationContext) }

    /**
     * @return This function an instance of GLSManager.
     * @author Ahmed Mones
     */
    fun create(): GLSManager {
        prepareAvailability(module = module)
        return object: GLSManager(
            repository = provideLocationRepository(module),
            serviceProvider = serviceProvider,
            isServicesAvailable = isServicesAvailable
        ){}
    }

    private fun prepareAvailability(module: GLSModule) {
        isServicesAvailable = with(module) {
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
        serviceProvider = service
    }

    private fun provideLocationRepository(module: GLSModule): LocationRepository =
        LocationRepositoryImpl(
            service = when (serviceProvider) {
                is Services.Google -> module.googleService
                is Services.Huawei -> module.huaweiService
                else -> NoneService()
            }
        )
}
