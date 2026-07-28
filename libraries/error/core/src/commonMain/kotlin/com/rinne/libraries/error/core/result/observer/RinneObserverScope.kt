package com.rinne.libraries.error.core.result.observer

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.RinneResultState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlin.coroutines.CoroutineContext

interface RinneObserverScope<T> {
    val coroutineContext: CoroutineContext //TODO remove coroutineContext field
    val coroutineScope: CoroutineScope get() = CoroutineScope(coroutineContext)

    suspend fun emit(data: T)
}

fun <T, R> RinneObserverScope<RinneResultState<R>>.observe(
    flow: Flow<T>,
    transform: suspend (value: T) -> RinneResultState<R>,
    withLoading: Boolean = true,
) {
    flow.onStart { if (withLoading) emit(RinneResultState.Loading) }
        .map { transform(it) }
        .onEach { emit(it) }
        .catch { emit(RinneResultState.Error(it.asRinneException())) }
//        .retry { throwable ->
//            true //TODO: it should not be always true
//        }
        .launchIn(coroutineScope)
}

fun <T> RinneObserverScope<RinneResultState<T>>.observe(
    flow: Flow<T>,
    withLoading: Boolean = true,
) {
    observe(
        flow = flow,
        transform = { RinneResultState.Success(it) },
        withLoading = withLoading
    )
}

fun <T> RinneObserverScope<RinneResultState<T>>.observeSilently(
    flow: Flow<*>,
    withLoading: Boolean = false,
    withErrors: Boolean = false,
) {
    flow
        .onStart { if (withLoading) emit(RinneResultState.Loading) }
        .retry { throwable ->
            if (withErrors) emit(RinneResultState.Error(throwable.asRinneException()))
            true //TODO: it should not be always true
        }
        .launchIn(coroutineScope)
}

internal class RinneObserverScopeImpl<T>(
    override val coroutineContext: CoroutineContext,
    private val onEmit: suspend (T) -> Unit,
) : RinneObserverScope<T> {

    override suspend fun emit(data: T) = onEmit(data)
}