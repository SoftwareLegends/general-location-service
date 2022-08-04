package com.gateway.gls.data

import com.gateway.gls.domain.models.Services

object GLServiceAvailability {
    var serviceProvider: Services = Services.None
    var isServicesAvailable: Boolean = false
}