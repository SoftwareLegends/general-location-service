package com.gateway.core.base

import com.gateway.core.exceptions.ResponseException
import com.gateway.core.mapper.HttpExceptionMapper
import retrofit2.HttpException
import retrofit2.Response

interface BaseRemoteDataSource {
    suspend fun <T, R> apiCall(
        suspendFunction: suspend () -> Response<T>,
        mapper: (T) -> R
    ) = try {
        checkIfSuccessful(result = suspendFunction(), mapper = mapper)
    } catch (t: HttpException) {
        throw httpExceptionMapper.map(from = t)
    }

    private fun <T, R> checkIfSuccessful(
        result: Response<T>,
        mapper: (T) -> R
    ): Resource<R> = if (result.isSuccessful)
        result.body()?.run {
            Resource.Success(mapper(this))
        } ?: Resource.Empty
    else
        throw ResponseException(
            message = result.errorBody()?.string().toString(),
            code = result.code()
        )

    private companion object {
        val httpExceptionMapper: HttpExceptionMapper = HttpExceptionMapper()
    }
}
