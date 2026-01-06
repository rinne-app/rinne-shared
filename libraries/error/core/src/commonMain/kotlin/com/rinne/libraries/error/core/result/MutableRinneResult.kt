package com.rinne.libraries.error.core.result

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.impl.MutableRinneResultImpl
import com.rinne.libraries.error.core.result.reseatable.RinneReseatableResult

interface MutableRinneResult<T> : RinneReseatableResult<T> {
    fun setState(newState: RinneResultState<T>)
}

fun <T> MutableRinneResult(state: RinneResultState<T> = RinneResultState.None): MutableRinneResult<T> =
    MutableRinneResultImpl(state)

fun <T> MutableRinneResult(data: T): MutableRinneResult<T> = MutableRinneResultImpl(RinneResultState.Success(data))

fun <T> MutableRinneResult<T>.setLoading() = setState(RinneResultState.Loading)
fun <T> MutableRinneResult<T>.set(data: T) = setState(RinneResultState.Success(data))
fun <T> MutableRinneResult<T>.set(throwable: Throwable) = setState(RinneResultState.Error(throwable.asRinneException()))