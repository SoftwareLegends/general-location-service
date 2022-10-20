package com.gateway.core.exceptions.base

abstract class BaseException : Throwable() {
    abstract override val message: String?
    abstract val code: Int
}
