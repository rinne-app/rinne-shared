package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import com.rinne.libraries.error.compose.result.new.RinneResultUiState.Type.*
import com.rinne.libraries.error.core.result.refreshable.RinneRefreshable
import com.rinne.libraries.error.core.result.reseatable.RinneReseatable

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
) : RinneResultUiState<R> {

    override val current: RinneResultUiState.Type<R> by derivedStateOf {
        when (val current = uiState.current) {
            is Error -> current
            Loading -> Loading
            None -> None
            is Success<T> -> Success(transformer.transform(current.data))
            is Updating<T> -> Updating(transformer.transform(current.data))
            is UpdateError<T> -> UpdateError(transformer.transform(current.data), current.error)
        }
    }
    override val refresher: RinneRefreshable? = uiState.refresher
    override val reseater: RinneReseatable? = uiState.reseater
}



