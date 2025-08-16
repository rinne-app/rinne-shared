package com.rinne.libraries.error.core.result

import kotlinx.coroutines.flow.StateFlow

interface RinneResult<T> {
    val stateFlow: StateFlow<RinneResultState<T>>
}

fun <T> RinneResult(
    state: RinneResultState<T> = RinneResultState.Loading
): RinneResult<T> = MutableRinneResult(state)

fun <T> RinneResult(
    data: T,
): RinneResult<T> = MutableRinneResult(RinneResultState.Success(data))

val <T> RinneResult<T>.state: RinneResultState<T>
    get() = stateFlow.value

fun <T> RinneResult<T>.getOrNull(): T? {
    return (state as? RinneResultState.Success)?.data
}

val RinneResult<*>.isSuccess get() = state is RinneResultState.Success
val RinneResult<*>.isFailure get() = state is RinneResultState.Error
val RinneResult<*>.isLoading get() = state is RinneResultState.Loading




