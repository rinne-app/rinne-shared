package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Stable
import com.rinne.libraries.error.compose.result.RinneResultUi
import com.rinne.libraries.error.core.RinneException
import com.rinne.libraries.error.core.result.refreshable.RinneRefreshable
import com.rinne.libraries.error.core.result.reseatable.RinneReseatable

@Stable
interface RinneResultUiState<T> {
    val current: Type<T>

    val refresher: RinneRefreshable?
    val reseater: RinneReseatable?

    sealed interface Type<out T> {
        sealed interface WithData<T> : Type<T> {
            val data: T
        }

        data object None : Type<Nothing>
        data object Loading : Type<Nothing>
        data class Error(val error: RinneException) : Type<Nothing>

        data class UpdateError<T>(
            override val data: T,
            val error: RinneException
        ) : WithData<T>

        data class Success<T>(override val data: T) : WithData<T>
        data class Updating<T>(override val data: T) : WithData<T>
    }
}

fun <T> RinneResultUiState.Type<T>.getDataOrNull() = (this as? RinneResultUiState.Type.WithData<T>)?.data

fun RinneResultUiState<*>.isRefreshable(): Boolean {
    return refresher != null
}

fun RinneResultUiState<*>.tryRefresh() {
    runCatching { refresher?.refresh() }
}

fun RinneResultUiState<*>.isReseatable(): Boolean {
    return reseater != null
}

fun RinneResultUiState<*>.tryReset() {
    runCatching { reseater?.reset() }
}