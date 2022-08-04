package com.gateway.gls.domain.models

sealed interface ServiceFailure {
    data class GpsProviderIsDisabled(
        val message: String? = "Gps provider is disabled, try to enable it",
        val code: Int = 200
    ) : ServiceFailure

    data class LocationNeverRecorded(
        val message: String? = "Location has never been recorded on this device",
        val code: Int = 201
    ) : ServiceFailure

    data class NetworkUnavailable(
        val message: String? = "Network is unavailable, location can not be requested",
        val code: Int = 202
    ) : ServiceFailure

    data class LocationServiceNotFound(
        val message: String? = "Location service not found",
        val code: Int = 404
    ) : ServiceFailure
}
