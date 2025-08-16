package com.rinne.libraries.error.compose.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rinne.libraries.error.compose.result.impl.RinneResultUiStateImpl
import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.state
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

//TODO
//class RinneResultUiMapper {
//    fun <T> map(result: RinneResultState<T>) = RinneResultUiState<T>()
//}

fun <T> RinneResultUiState(
    mutableAppResult: MutableRinneResult<T> = MutableRinneResult(),
    defaultState: RinneResultState<T> = mutableAppResult.state,
    withLoader: Boolean = true,
): RinneResultUiState<T> = RinneResultUiStateImpl(
    mutableAppResult, defaultState, withLoader
)

@Stable
interface RinneResultUiState<T> {
    val mutableAppResult: MutableRinneResult<T>
    val stateFlow: StateFlow<RinneResultUi<T>> //TODO StateFlow

    fun reset()
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