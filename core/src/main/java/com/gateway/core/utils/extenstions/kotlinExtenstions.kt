package com.gateway.core.utils.extenstions


fun <T, R> T?.onNull(block: () -> R): R? =
    if (this != null) block() else null
