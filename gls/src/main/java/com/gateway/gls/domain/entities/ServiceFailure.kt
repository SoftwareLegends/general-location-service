package com.gateway.gls.domain.entities

import com.gateway.core.exceptions.base.BaseException

sealed class ServiceFailure(
    override val message: String?,
    override val code: Int
) : BaseException() {
    data class GpsProviderIsDisabled(
        override val message: String? = "Gps provider is disabled, try to enable it",
        override val code: Int = 200
    ) : ServiceFailure(message, code)

    data class LocationNeverRecorded(
        override val message: String? = "Location has never been recorded on this device",
        override val code: Int = 201
    ) : ServiceFailure(message, code)

    data class NetworkUnavailable(
        override val message: String? = "Network is unavailable, location can not be requested",
        override val code: Int = 202
    ) : ServiceFailure(message, code)

    data class LocationServiceNotFound(
        override val message: String? = "Location service not found",
        override val code: Int = 404
    ) : ServiceFailure(message, code)

    data class UnknownError(
        override val message: String? = "Unknown Error",
        override val code: Int = 520
    ) : ServiceFailure(message, code)
}
