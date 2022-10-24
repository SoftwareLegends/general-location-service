package com.gateway.core.exceptions

data class JsonSyntaxException(
    override val message: String?,
) : Throwable()
