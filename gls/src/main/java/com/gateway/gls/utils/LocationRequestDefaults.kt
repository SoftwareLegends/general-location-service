package com.gateway.gls.utils

internal object LocationRequestDefaults {
    const val UPDATE_INTERVAL_MILLIS: Long = 750
    const val MIN_UPDATE_INTERVAL_MILLIS: Long = UPDATE_INTERVAL_MILLIS / 2
    const val MAX_UPDATES: Int = Int.MAX_VALUE
    const val MAX_UPDATE_DELAY_MILLIS: Long = 150
    const val MIN_UPDATE_DISTANCE_METERS: Float = 0f
    const val SAFE_COUNTER: Int = 15
}
