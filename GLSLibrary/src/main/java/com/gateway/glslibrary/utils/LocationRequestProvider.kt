package com.gateway.glslibrary.utils


import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationRequest as LocationRequestGoogle
import com.huawei.hms.location.LocationRequest as LocationRequestHuawei

sealed class LocationRequestProvider<out T> {
    object Google : LocationRequestProvider<LocationRequestGoogle>() {
        val locationRequest: LocationRequestGoogle = LocationRequestGoogle.create().apply {
            priority = Priority.PRIORITY_HIGH_ACCURACY
            interval = Constant.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = Constant.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            numUpdates = Constant.NUMBER_OF_UPDATES
        }
    }

    object Huawei : LocationRequestProvider<LocationRequestHuawei>() {
        val locationRequest: LocationRequestHuawei = LocationRequestHuawei.create().apply {
            priority = LocationRequestHuawei.PRIORITY_HIGH_ACCURACY
            interval = Constant.UPDATE_INTERVAL_IN_MILLISECONDS
            fastestInterval = Constant.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            numUpdates = Constant.NUMBER_OF_UPDATES
        }
    }
}