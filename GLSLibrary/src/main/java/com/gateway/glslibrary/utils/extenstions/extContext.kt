package com.gateway.glslibrary.utils.extenstions

import android.content.Context
import android.location.LocationManager
import com.gateway.glslibrary.domain.models.Error
import com.gateway.glslibrary.utils.enums.LocationFailure

fun Context.isGpsProviderEnabled(): Boolean {
    val locationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
