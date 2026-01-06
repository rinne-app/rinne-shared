package com.rinne.libraries.error.compose.result.impl

import androidx.compose.runtime.Stable
import com.rinne.libraries.error.compose.result.MutableRinneResultUiState
import com.rinne.libraries.error.compose.result.RinneResultUi
import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.state
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

@Stable
internal class RinneResultUiStateImpl<T>(
    override val mutableAppResult: MutableRinneResult<T> = MutableRinneResult(),
    private val defaultState: RinneResultState<T> = mutableAppResult.state,
    private val withLoader: Boolean = true, //TODO improve
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob()), //TODO
) : MutableRinneResultUiState<T> {

    override val stateFlow = mutableAppResult
        .stateFlow
        .scan<RinneResultState<T>, RinneResultUi<T>>(RinneResultUi.Loading) { old, new ->
            new.asUi(withLoader = withLoader, old = old)
        }
//        .onEach { RootRinneLogger.default.i("newState: $it") }
        .stateIn(
            coroutineScope,
            SharingStarted.Lazily,
            defaultState.asUi(withLoader = withLoader)
        ) //TODO

    override fun reset() {
        mutableAppResult.setState(defaultState)
    }
}

private fun <T> RinneResultState<T>.asUi(
    withLoader: Boolean,
    old: RinneResultUi<T>? = null
): RinneResultUi<T> {
    return when (this) {
        is RinneResultState.Error -> RinneResultUi.Error(error.asRinneException())
        RinneResultState.Loading, RinneResultState.None -> when {
            !withLoader && old != null -> old
            old is RinneResultUi.Success -> RinneResultUi.Updating(old.data)
            else -> RinneResultUi.Loading
        }

        is RinneResultState.Success<T> -> RinneResultUi.Success(data)
    }
}