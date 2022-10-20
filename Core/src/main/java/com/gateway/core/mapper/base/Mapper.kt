package com.gateway.core.mapper.base

interface Mapper<in I, out O> {
    fun map(from: I): O
}
