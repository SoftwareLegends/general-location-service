package com.gateway.gls.domain.models

sealed class Priority(val value: Int) {
    object HighAccuracy : Priority(value = 100)
    object BalancedPowerAccuracy : Priority(value = 102)
    object LowPower : Priority(value = 104)
    object Passive : Priority(value = 105)
}
