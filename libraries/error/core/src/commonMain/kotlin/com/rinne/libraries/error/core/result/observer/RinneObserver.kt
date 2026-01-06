package com.rinne.libraries.error.core.result.observer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface RinneObserver<T> {
    fun observeIn(coroutineScope: CoroutineScope, action: suspend (T) -> Unit = {})
}

private class RinneObserverImpl<T>(
    private val block: suspend RinneObserverScope<T>.() -> Unit
) : RinneObserver<T> {

    val flow = flow {
        val scope = RinneObserverScopeImpl(
            coroutineContext = currentCoroutineContext(),
            onEmit = { emit(it) },
        )

        block(scope)
    }

    override fun observeIn(
        coroutineScope: CoroutineScope,
        action: suspend (T) -> Unit
    ) {
        flow.onEach(action).launchIn(coroutineScope)
    }
}
