package com.rinne.libraries.error.compose.result

import androidx.compose.runtime.Stable
import com.rinne.libraries.error.core.result.MutableRinneResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun interface RinneResultUiStateTransformer<T, R> {
    fun transform(data: T): R
}


fun <T, R> RinneResultUiState<T>.map(transformer: RinneResultUiStateTransformer<T, R>): RinneResultUiState<R> {
    return RinneTransformableResultUiState(this, transformer)
}

@Stable
private class RinneTransformableResultUiState<T, R>(
    private val uiState: RinneResultUiState<T>,
    private val transformer: RinneResultUiStateTransformer<T, R>,
    coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob()),
) : RinneResultUiState<R> {
    override val mutableAppResult: MutableRinneResult<R> = MutableRinneResult()
    override val stateFlow: StateFlow<RinneResultUi<R>> = uiState
        .stateFlow
        .map {
            when (it) {
                is RinneResultUi.Error -> RinneResultUi.Error(it.error)
                RinneResultUi.Loading -> RinneResultUi.Loading
                is RinneResultUi.Success<T> -> RinneResultUi.Success(transformer.transform(it.data))
                is RinneResultUi.Updating<T> -> RinneResultUi.Updating(transformer.transform(it.data))
            }
        }
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5000),
            RinneResultUi.Loading
        )

    override fun reset() = uiState.reset()
}

/*
//TODO replace

fun <T, R> SnoreUiState<T>.map(transformer: SnoreUiStateTransformer<T, R>): SnoreUiState<R> {
    return TransformableSnoreUiState(this, transformer)
}

@Stable
internal class TransformableSnoreUiState<T, R>(
    private val state: SnoreUiState<T>,
    private val transformer: SnoreUiStateTransformer<T, R>,
) : SnoreUiState<R> {
    override val currentState: SnoreUiState.Type<R>
        get() = when (val current = state.currentState) {
            is WithData -> when (current) {
                is Success -> Success(
                    transformer.transform(
                        current.data
                    )
                )

                is Updating -> Updating(
                    transformer.transform(
                        current.data
                    )
                )
            }

            is Error -> Error(current.error)
            Loading -> Loading
        }

    override fun setResult(result: EinResult<R>) {
        error("Unsupported operation") // TODO separate SnoreUiState and MutableSnoreUiState
    }

    override fun updateData(transform: (R) -> R) {
        error("Unsupported operation") // TODO separate SnoreUiState and MutableSnoreUiState
    }

    override fun getDataOrNull(): R? {
        return state.getDataOrNull()?.let(transformer::transform)
    }

    override fun reset() {
        state.reset()
    }
}

 */