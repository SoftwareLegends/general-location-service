package com.gateway.gls.utils.extenstions

import android.content.Context
import android.location.LocationManager

fun Context.isGpsProviderEnabled(): Boolean {
    val locationManager =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}
