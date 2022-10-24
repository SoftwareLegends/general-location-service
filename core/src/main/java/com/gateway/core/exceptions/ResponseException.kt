package com.gateway.core.exceptions

import com.gateway.core.exceptions.base.BaseException

data class ResponseException(
    override val message: String?,
    override val code: Int
) : BaseException()
