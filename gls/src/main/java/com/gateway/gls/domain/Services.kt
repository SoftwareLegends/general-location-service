package com.gateway.gls.domain

sealed class Services {
    object Google : Services()
    object Huawei : Services()
    object None : Services()
}