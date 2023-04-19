package com.gateway.gls.utils.extenstions

import android.location.Location


infix fun Location?.isEqual(other: Location?) = this != null
        && other != null
        && longitude == other.longitude
        && latitude == other.latitude
        && altitude == other.altitude
