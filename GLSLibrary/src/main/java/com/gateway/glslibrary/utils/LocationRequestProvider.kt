package com.gateway.glslibrary.utils


import com.huawei.hms.location.LocationRequest as LocationRequestHuawei

sealed class LocationRequestProvider<out T> {

    object Huawei : LocationRequestProvider<LocationRequestHuawei>() {
        val locationRequest: LocationRequestHuawei = LocationRequestHuawei.create().apply {
            priority = LocationRequestHuawei.PRIORITY_HIGH_ACCURACY
            interval = Constant.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = Constant.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            numUpdates = Constant.NUMBER_OF_UPDATES
        }
    }
}