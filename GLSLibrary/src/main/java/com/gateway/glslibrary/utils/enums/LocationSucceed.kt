package com.gateway.glslibrary.utils.enums

enum class LocationSucceed(
    val message: String,
    val code: Int
) {
    CACHED_LOCATION_SUCCEED("Cached location succeed", 100),
    LAST_KNOWN_LOCATION_SUCCEED("Last known location succeed", 101),
    REQUEST_UPDATE_SUCCEED("Request location update succeed", 102)
}