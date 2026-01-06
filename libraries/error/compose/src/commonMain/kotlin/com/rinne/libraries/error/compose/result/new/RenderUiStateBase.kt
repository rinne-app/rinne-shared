package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.rinne.libraries.error.core.RinneException

interface RenderWithDataUiStateScope {
    fun showContent(show: Boolean = true)
}

private class RenderWithDataUiStateScopeImpl : RenderWithDataUiStateScope {
    var isContentVisible by mutableStateOf(false)

    override fun showContent(show: Boolean) {
        isContentVisible = show
    }
}

@Composable
fun <T> RenderUiStateBase(
    uiState: RinneResultUiState<T>,
    none: @Composable () -> Unit,
    loading: @Composable () -> Unit,
    error: @Composable (state: RinneResultUiState<T>, error: RinneException) -> Unit,
    updateError: @Composable RenderWithDataUiStateScope.(state: RinneResultUiState<T>, data: T, error: RinneException) -> Unit,
    updating: @Composable RenderWithDataUiStateScope.(T) -> Unit,
    content: @Composable (T) -> Unit,
) {
    when (val current = uiState.current) {
        is RinneResultUiState.Type.Error -> error(uiState, current.error)
        RinneResultUiState.Type.Loading -> loading()
        RinneResultUiState.Type.None -> none()
        is RinneResultUiState.Type.WithData<T> -> {
            val scope = remember(current) { RenderWithDataUiStateScopeImpl() }
            if (current is RinneResultUiState.Type.Success<T> || scope.isContentVisible) content(current.data)

            when (current) {
                is RinneResultUiState.Type.UpdateError<T> -> updateError.invoke(
                    scope,
                    uiState,
                    current.data,
                    current.error
                )

                is RinneResultUiState.Type.Updating<T> -> updating.invoke(scope, current.data)
                is RinneResultUiState.Type.Success<T> -> {
                    /* no-op */
                }
            }
        }
    }
}