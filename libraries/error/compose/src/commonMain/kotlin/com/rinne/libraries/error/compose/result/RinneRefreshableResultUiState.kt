package com.rinne.libraries.error.compose.result

import androidx.compose.runtime.Stable
import com.rinne.libraries.error.compose.result.impl.RinneRefreshableResultUiStateImpl
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.state
import kotlinx.coroutines.flow.SharedFlow

@Deprecated("Use com.rinne.libraries.error.compose.result.new.RinneResultUiState")
fun <T> RinneRefreshableResultUiState(
    mutableAppResult: MutableRinneResult<T> = MutableRinneResult(),
    defaultState: RinneResultState<T> = mutableAppResult.state,
    withLoader: Boolean = true,
): MutableRinneRefreshableResultUiState<T> = RinneRefreshableResultUiStateImpl(
    RinneResultUiState(mutableAppResult, defaultState, withLoader)
)

@Stable
@Deprecated("Use com.rinne.libraries.error.compose.result.new.RinneResultUiState")
interface RinneRefreshableResultUiState<T> : RinneResultUiState<T> {
    val refreshSharedFlow: SharedFlow<Unit>

    suspend fun refresh()
}

@Stable
@Deprecated("Use com.rinne.libraries.error.compose.result.new.RinneResultUiState")
interface MutableRinneRefreshableResultUiState<T> : RinneRefreshableResultUiState<T>, MutableRinneResultUiState<T>
