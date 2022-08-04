package com.gateway.gls.domain.interfaces

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.annotation.RequiresPermission
import com.gateway.gls.domain.models.Resource
import com.gateway.gls.domain.models.Error
import com.gateway.gls.utils.enums.LocationFailure
import com.gateway.gls.utils.extenstions.isGpsProviderEnabled
import com.gateway.gls.utils.extenstions.toModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber


interface LocationService {
    @RequiresPermission(
        allOf = [Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION]
    )
    suspend fun lastLocation(): Resource<Location>

    fun requestLocationUpdates() : Flow<Resource<Location>>

    fun locationSettings(resultContracts: ActivityResultLauncher<IntentSenderRequest>)

    suspend fun <T> safeCall(block: suspend () -> Resource<T>) = catchError { block() }

    fun getLocationResult(context: Context, location: Location?): Resource<Location> {
        location?.let {
            return Resource.Success(data = location)
        }

        return if (context.isGpsProviderEnabled().not())
            Resource.Fail(error = LocationFailure.GPS_PROVIDER_IS_DISABLED.toModel())
        else
            Resource.Fail(error = LocationFailure.LOCATION_NEVER_RECORDED.toModel())
    }

    private suspend fun <T> catchError(block: suspend () -> Resource<T>) = try {
        block()
    } catch (e: Exception) {
        Timber.e(e)
        Resource.Fail(error = Error(message = e.message))
    }
}
