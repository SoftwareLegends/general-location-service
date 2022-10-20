package com.gateway.core.mapper

import com.gateway.core.exceptions.base.Error
import com.gateway.core.base.Resource
import com.gateway.core.exceptions.GpsProviderIsDisabledException
import com.gateway.core.exceptions.HttpException
import com.gateway.core.exceptions.ResponseException
import java.io.IOException

fun Error.toResource() = Resource.Fail(error = this)

fun Throwable.toUnKnownError() = Error.UnknownError(message = message)

fun IOException.toNetworkError() = Error.NetworkError()

fun HttpException.toHttpError() = Error.HttpError(message = message, code = code)

fun ResponseException.toResponseError() = Error.ResponseError(message = message, code = code)

fun GpsProviderIsDisabledException.toGpsError() = Error.GpsError(message = message, code = code)
