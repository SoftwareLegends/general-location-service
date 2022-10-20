package com.gateway.gls.di

import android.content.Context
import com.gateway.gls.data.services.ServiceAvailability
import com.gateway.gls.domain.entities.Services
import com.google.android.gms.common.ConnectionResult
import timber.log.Timber

object GLSInitializer {
    @Volatile
    internal lateinit var applicationContext: Context

    /**
     * This function should called in MainActivity or an Application instance.

     * to provide an applicationContext instance for the service.
     *
     * @author Ahmed Mones
     */
    fun init(applicationContext: Context) {
        if (this::applicationContext.isInitialized.not()) {
            synchronized(applicationContext) { this.applicationContext = applicationContext }
            prepareAvailability(context = applicationContext)
        }
    }

    private fun prepareAvailability(context: Context) {
        ServiceAvailability.isServicesAvailable = with(GLSModule){
            when (ConnectionResult.SUCCESS) {
                googleApiAvailability.isGooglePlayServicesAvailable(context) -> setServiceProvider(
                    service = Services.Google
                )
                huaweiApiAvailability.isHuaweiMobileServicesAvailable(context) -> setServiceProvider(
                    service = Services.Huawei
                )
                else -> false
            }
        }
    }

    private fun setServiceProvider(service: Services) = true.also {
        Timber.d(service::class.java.name.substringAfter('$'))
        ServiceAvailability.serviceProvider = service
    }
}
