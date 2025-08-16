package com.rinne.libraries.error.compose.result.impl

import androidx.compose.runtime.Stable
import com.rinne.libraries.error.compose.result.RinneRefreshableResultUiState
import com.rinne.libraries.error.compose.result.RinneResultUiState
import com.rinne.libraries.logger.core.extensions.d
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Stable
internal class RinneRefreshableResultUiStateImpl<T>(
    private val rinneResultUiState: RinneResultUiState<T>,
) : RinneRefreshableResultUiState<T> {
    private val _refreshSharedFlow = MutableSharedFlow<Unit>()
    override val refreshSharedFlow = _refreshSharedFlow.asSharedFlow()

    override val mutableAppResult get() = rinneResultUiState.mutableAppResult
    override val stateFlow get() = rinneResultUiState.stateFlow

    override fun reset() {
        rinneResultUiState.reset()
    }

    override suspend fun refresh() {
        _refreshSharedFlow.emit(Unit)
    }
}