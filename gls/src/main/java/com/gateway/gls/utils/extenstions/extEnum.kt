package com.gateway.gls.utils.extenstions

import com.gateway.gls.domain.models.Error
import com.gateway.gls.utils.enums.LocationFailure

fun LocationFailure.toModel() = Error(message = message, code = code)
