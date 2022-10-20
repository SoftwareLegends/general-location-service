package com.gateway.core.utils


typealias CallBack<T> = (T) -> Unit
typealias VoidCallBack = () -> Unit
typealias ValueChangedCallBack<T> = CallBack<T>
typealias ResultCallBack<I, O> = (I) -> O
typealias ResultCallBackNoArgs<T> = () -> T
typealias SuspendResultCallBackNoArgs<T> = suspend () -> T
