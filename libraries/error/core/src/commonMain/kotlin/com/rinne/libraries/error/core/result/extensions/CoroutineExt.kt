package com.rinne.libraries.error.core.result.extensions

import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.withResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> CoroutineScope.asyncWithResult(
    result: MutableRinneResult<T> = MutableRinneResult(),
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    withLoading: Boolean = true,
    block: suspend CoroutineScope.() -> T,
): Deferred<RinneResult<T>> {
    return async(
        context = context,
        start = start,
    ) {
        withResult(result = result, withLoading = withLoading) { block() }
    }
}

fun <T> CoroutineScope.launchWithResult(
    result: MutableRinneResult<T> = MutableRinneResult(),
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    withLoading: Boolean = true,
    block: suspend CoroutineScope.() -> T,
): Job {
    return launch(
        context = context,
        start = start,
    ) {
        withResult(result = result, withLoading = withLoading) { block() }
    }
}