package com.gateway.core.mapper

import com.gateway.core.exceptions.HttpException
import com.gateway.core.mapper.base.Mapper

/**
 * Convert retrofit HttpException to Domain exception
 * */
internal class HttpExceptionMapper : Mapper<retrofit2.HttpException, HttpException> {
    override fun map(from: retrofit2.HttpException): HttpException {
        return HttpException(
            message = from.response()?.errorBody()?.string(),
            code = from.code()
        )
    }
}
