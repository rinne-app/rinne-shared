package com.rinne.libraries.error.core.result.extensions

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.withResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry

fun <T, R> Flow<T>.mapAsRinneResult(
    result: MutableRinneResult<R> = MutableRinneResult(),
    withLoading: Boolean = true,
    transform: suspend (value: T) -> R
): Flow<RinneResult<R>> {
    return map<T, RinneResult<R>> { value: T ->
        withResult(result, withLoading) { transform.invoke(value) }
    }
}

fun <T> Flow<T>.asRinneResult(
    result: MutableRinneResult<T> = MutableRinneResult(),
    withLoading: Boolean = true,
//    TODO rework retry it can cause error (infinity retry)
    retry: suspend (cause: Throwable) -> Boolean = { true },
): Flow<RinneResult<T>> {
    return map<T, RinneResult<T>> {
        result.setState(RinneResultState.Success(it))
        result
    }.onStart {
        if (withLoading) result.setState(RinneResultState.Loading)
        emit(result)
        result
    }.catch { throwable ->
        if (withLoading) result.setState(RinneResultState.Error(throwable.asRinneException()))
        emit(result)
    }.retry(predicate = retry)
}