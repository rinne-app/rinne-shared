package com.rinne.libraries.error.core.result

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.impl.MutableRinneResultImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

fun <T> MutableRinneResult(
    state: RinneResultState<T> = RinneResultState.Loading
): MutableRinneResult<T> = MutableRinneResultImpl(state)

fun <T> MutableRinneResult(
    data: T,
): MutableRinneResult<T> = MutableRinneResultImpl(RinneResultState.Success(data))


interface MutableRinneResult<T> : RinneResult<T> {
    fun setState(newState: RinneResultState<T>)
}

fun <T> MutableRinneResult<T>.setLoading() = setState(RinneResultState.Loading)
fun <T> MutableRinneResult<T>.set(data: T) = setState(RinneResultState.Success(data))
fun <T> MutableRinneResult<T>.set(throwable: Throwable) = setState(RinneResultState.Error(throwable))


fun <T> MutableRinneResult<T>.observe(
    flow: Flow<T>,
    withLoading: Boolean = true,
) = flow
    .onStart { if (withLoading) setState(RinneResultState.Loading) }
    .onEach { setState(RinneResultState.Success(it)) }
    .catch { throwable -> setState(RinneResultState.Error(throwable.asRinneException())) }

