package com.gateway.gls.data.services

import com.gateway.gls.domain.entities.Services

internal object ServiceAvailability {
    var serviceProvider: Services = Services.None
    var isServicesAvailable: Boolean = false
}
