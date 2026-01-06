package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.RinneResultState
import com.rinne.libraries.error.core.result.refreshable.RinneRefreshable
import com.rinne.libraries.error.core.result.reseatable.RinneReseatable
import com.rinne.libraries.logger.core.EmptyRinneLogger
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.RootRinneLogger
import com.rinne.libraries.logger.core.extensions.i

internal class RinneResultUiStateImpl<T> private constructor(
    override val refresher: RinneRefreshable?,
    override val reseater: RinneReseatable?
) : RinneResultUiState<T> {
    private var lastData by mutableStateOf<T?>(null)
    override var current: RinneResultUiState.Type<T> by mutableStateOf(RinneResultUiState.Type.Loading)
        private set


    companion object {
        @Composable
        fun <T> remember(
            result: RinneResult<T>,
            loggerTag: String? = null,
        ): RinneResultUiState<T> {
            val uiState = retain {
                RinneResultUiStateImpl<T>(
                    refresher = result as? RinneRefreshable,
                    reseater = result as? RinneReseatable,
                )
            }
            val logger = provideRinneResultUiStateLogger(loggerTag)
            val resultState by result.stateFlow.collectAsStateWithLifecycle()

            LaunchedEffect(resultState, uiState.lastData) {
                val type = resultState.asRinneResultUiStateType(uiState.lastData)

                (type as? RinneResultUiState.Type.WithData<T>)?.data?.let { uiState.lastData = it }

                logger.i("uiState.current: $type")
                uiState.current = type
            }

            return uiState
        }
    }
}


private fun <T> RinneResultState<T>.asRinneResultUiStateType(
    lastData: T? = null,
): RinneResultUiState.Type<T> {
    return when (this) {
        is RinneResultState.Error -> RinneResultUiState.Type.Error(error)
        RinneResultState.Loading -> when (lastData == null) {
            true -> RinneResultUiState.Type.Loading
            false -> RinneResultUiState.Type.Updating(lastData)
        }

        RinneResultState.None -> RinneResultUiState.Type.None
        is RinneResultState.Success<T> -> RinneResultUiState.Type.Success(data)
    }
}

private fun provideRinneResultUiStateLogger(tag: String?): RinneLogger {
    return tag?.let { RootRinneLogger.createNewLogger(it) } ?: EmptyRinneLogger
}