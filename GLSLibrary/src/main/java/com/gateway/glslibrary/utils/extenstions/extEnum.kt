package com.gateway.glslibrary.utils.extenstions

import com.gateway.glslibrary.domain.models.Error
import com.gateway.glslibrary.utils.enums.LocationFailure

fun LocationFailure.toModel() = Error(message = message, code = code)
