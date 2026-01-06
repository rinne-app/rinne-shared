package com.rinne.libraries.error.core.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface RinneResult<T> {
    val stateFlow: StateFlow<RinneResultState<T>>
}

//interface RinneResultNew<T> {
//    val flow: Flow<Type<T>>
//
//    sealed interface Type<out T> {
//        data object None : Type<Nothing>
//        data object Loading : Type<Nothing>
//        data class Error(val error: Throwable) : Type<Nothing>
//        data class Success<T>(val data: T) : Type<T>
//    }
//}

fun <T> RinneResult(
    state: RinneResultState<T> = RinneResultState.None
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
val RinneResult<*>.isNone get() = state is RinneResultState.None




