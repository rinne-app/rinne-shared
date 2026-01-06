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