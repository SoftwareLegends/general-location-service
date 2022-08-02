package com.gateway.glslibrary.utils.enums

enum class LocationFailure(
    val message: String,
    val code: Int
) {
    GPS_PROVIDER_IS_DISABLED("Gps provider is disabled, try to enable it", 200),
    LOCATION_NEVER_RECORDED("Location has never been recorded on this device", 201),
    NETWORK_UNAVAILABLE("Network is unavailable, location can not be requested", 202),
    LOCATION_SERVICE_NOT_FOUND("Location service not found", 404)
}