package com.rinne.libraries.error.compose.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rinne.libraries.error.compose.result.impl.RinneResultUiStateImpl
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.isLoading
import com.rinne.libraries.error.core.result.state
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

//TODO
//class RinneResultUiMapper {
//    fun <T> map(result: RinneResultState<T>) = RinneResultUiState<T>()
//}

fun <T> RinneResultUiState(
    mutableAppResult: MutableRinneResult<T> = MutableRinneResult(),
    defaultState: RinneResultState<T> = mutableAppResult.state,
    withLoader: Boolean = true,
): MutableRinneResultUiState<T> = RinneResultUiStateImpl(
    mutableAppResult, defaultState, withLoader
)

@Stable
interface RinneResultUiState<T> {
    val mutableAppResult: MutableRinneResult<T>
    val stateFlow: StateFlow<RinneResultUi<T>> //TODO StateFlow

    fun reset()
}

@Stable
interface MutableRinneResultUiState<T> : RinneResultUiState<T> {
    override val mutableAppResult: MutableRinneResult<T>
}

//TODO implement
@Stable
interface RinneResultObservableUiState<T> {
    fun launchIn(coroutineScope: CoroutineScope)
    fun observe(flow: Flow<T>)
}

//TODO improve, add lifecycle handling, check collectAsStateWithLifecycle
@Composable
fun <T> MutableRinneResultUiState<T>.collectIsLoadingState(): State<Boolean> {
    return produceState(mutableAppResult.isLoading) {
        mutableAppResult.stateFlow.collect { value = it == RinneResultState.Loading }
    }
}

suspend fun <T> RinneResultUiState<T>.tryRefresh() {
    (this as? RinneRefreshableResultUiState)?.refresh()
}

fun <T> RinneResultUiState<T>.getOrNull(): T? {
    return when (val value = stateFlow.value) {
        is RinneResultUi.WithData<T> -> value.data
        else -> null
    }
}

@Composable
fun <T> RinneResultUiState<T>.collectAsStateWithLifecycle(): State<RinneResultUi<T>> {
    return stateFlow.collectAsStateWithLifecycle()
}