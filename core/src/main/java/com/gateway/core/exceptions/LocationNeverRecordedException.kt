package com.gateway.core.exceptions

import com.gateway.core.exceptions.base.BaseException

data class LocationNeverRecordedException(
    override val message: String?,
    override val code: Int
): BaseException()
