package com.gateway.core.base

import com.gateway.core.exceptions.base.Error


sealed class Resource<out T> {
    object Init : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    object Empty : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Fail(val error: Error) : Resource<Nothing>()

    val toData: T? get() = (this as? Success)?.data
    val toError: Error? get() = (this as? Fail)?.error
    val isEmpty: Boolean get() = this is Empty
    val isFail: Boolean get() = this is Fail
    val isSuccess: Boolean get() = this is Success
    val isLoading: Boolean get() = this is Loading
}
