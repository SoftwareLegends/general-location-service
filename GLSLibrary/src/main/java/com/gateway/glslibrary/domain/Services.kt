package com.gateway.glslibrary.domain

sealed class Services {
    object Google : Services()
    object Huawei : Services()
    object None : Services()
}