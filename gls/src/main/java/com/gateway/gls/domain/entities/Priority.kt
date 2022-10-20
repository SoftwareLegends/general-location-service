package com.gateway.gls.domain.entities

enum class Priority(val value: Int) {
    HighAccuracy(value = 100),
    BalancedPowerAccuracy(value = 102),
    LowPower(value = 104),
    Passive(value = 105),
}
