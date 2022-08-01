package com.gateway.glslibrary.domain

import com.gateway.glslibrary.domain.models.Error


sealed class Resource<out T> {
    object Init : Resource<Nothing>()
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Fail(val data: Error? = null) : Resource<Nothing>()

    val toData get() : T? = (this as? Success)?.data
    val toError get() : Error? = (this as? Fail)?.data
    val isLoading get() = this is Loading
}