package com.gateway.core.usecase.base

import com.gateway.core.exceptions.GpsProviderIsDisabledException
import com.gateway.core.exceptions.HttpException
import com.gateway.core.exceptions.ResponseException
import com.gateway.core.exceptions.base.Error
import com.gateway.core.mapper.toGpsError
import com.gateway.core.mapper.toHttpError
import com.gateway.core.mapper.toNetworkError
import com.gateway.core.mapper.toResponseError
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class BaseUseCase<T> {

    protected suspend fun <returnType> FlowCollector<T>.timeoutEmit(
        timeoutMs: Long,
        onStartedState: T? = null,
        onEmptyState: T? = null,
        onSuccessState: ((returnType) -> T)? = null,
        onErrorState: ((Error) -> T)? = null,
        suspendFunction: suspend () -> returnType
    ) = runCatching {
        withTimeout(timeoutMs) {
            onStartedState?.let { state ->
                emit(state)
            }

            return@withTimeout suspendFunction()
        }
    }.onSuccess { result ->
        onSuccessState?.takeIf { result != null }
            ?.let { wrapper ->
                emit(wrapper(result))
            }

        onEmptyState?.takeUnless { result != null }?.let { state ->
            emit(state)
        }
    }.onFailure { t ->
        val errorMessage = when (t) {
            is IOException -> t.toNetworkError()
            is HttpException -> t.toHttpError()
            is ResponseException -> t.toResponseError()
            is TimeoutCancellationException -> Error.TimeoutError()
            is GpsProviderIsDisabledException -> t.toGpsError()
            else -> throw t
        }

        onErrorState?.let { wrapper ->
            emit(wrapper(errorMessage))
        }
    }

    protected companion object {
        val defaultTimeoutMs = TimeUnit.SECONDS.toMillis(30)
    }
}
