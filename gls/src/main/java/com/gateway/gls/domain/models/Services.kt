package com.gateway.gls.domain.models

sealed class Services {
    object Google : Services()
    object Huawei : Services()
    object None : Services()
}