package com.rinne.libraries.error.core.result.observer

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.i
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

interface RinneResultObserver<T> : RinneObserver<RinneResultState<T>> {
    val stateFlow: StateFlow<RinneResultState<T>>
}

suspend fun <T> RinneResultObserver<T>.awaitData(): T {
    return stateFlow.filterIsInstance<RinneResultState.Success<T>>().map { it.data }.first()
}


fun <T> RinneResultObserver(
    initialValue: RinneResultState<T> = RinneResultState.None,
    block: suspend RinneObserverScope<RinneResultState<T>>.() -> Unit
): RinneResultObserver<T> = RinneResultObserverImpl(initialValue = initialValue, block = block)

internal class RinneResultObserverImpl<T>(
    initialValue: RinneResultState<T> = RinneResultState.None,
    private val block: suspend RinneObserverScope<RinneResultState<T>>.() -> Unit
) : RinneResultObserver<T> {
    private val coroutineScope = CoroutineScope(SupervisorJob())

    //TODO channelFlow vs flow
    //To mitigate this restriction please use 'channelFlow' builder instead of 'flow')' has been detected.
    override val stateFlow = channelFlow<RinneResultState<T>> {
        val scope = RinneObserverScopeImpl<RinneResultState<T>>(
            coroutineContext = currentCoroutineContext(),
            onEmit = {
                channel.send(it)
            },
        )

        runCatching {
            block(scope)
        }.onFailure {
            //TODO: need to continue block execution even if error
            channel.send(RinneResultState.Error(it.asRinneException()))
        }
    }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), initialValue)

    override fun observeIn(
        coroutineScope: CoroutineScope,
        action: suspend (RinneResultState<T>) -> Unit
    ) {
        stateFlow.onEach(action).launchIn(coroutineScope)
    }
}

fun <T> RinneResultObserver<T>.observeAsRinneResult(
    coroutineScope: CoroutineScope,
    initialState: RinneResultState<T> = RinneResultState.None
): RinneResult<T> {
    val result = MutableRinneResult(initialState)

    observeIn(coroutineScope) {
        result.setState(it)
    }

    return result
}
