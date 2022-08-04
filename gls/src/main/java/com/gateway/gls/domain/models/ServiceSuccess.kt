package com.gateway.gls.domain.models

sealed interface ServiceSuccess {
    data class CachedLocationSucceed(
        val message: String? = "Cached location succeed",
        val code: Int = 100
    ) : ServiceSuccess

    data class LastKnownLocationSucceed(
        val message: String? = "Last known location succeed",
        val code: Int = 101
    ) : ServiceSuccess

    data class RequestLocationUpdateSucceed(
        val message: String? = "Request location update succeed",
        val code: Int = 102
    ) : ServiceSuccess
}
