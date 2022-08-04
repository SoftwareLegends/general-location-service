package com.gateway.gls.domain.models


sealed class Resource<out T> {
    object Init : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Fail(val error: Error? = null) : Resource<Nothing>()

    val toData get() : T? = (this as? Success)?.data
    val toError get() : Error? = (this as? Fail)?.error
    val isLoading get() = this is Loading
}