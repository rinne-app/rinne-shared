package com.rinne.libraries.error.compose.result

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

@Deprecated("")
fun <T> RinneResultUiState<T>.observe(
    flow: Flow<T>,
    withLoading: Boolean = true,
): Flow<T> {
    if (this is RinneRefreshableResultUiState) return observe(flow, withLoading)

    return flow
//    .flowWithLifecycle() TODO should be with flowWithLifecycle
}


@OptIn(ExperimentalCoroutinesApi::class)
fun <T> RinneRefreshableResultUiState<T>.observe(
    flow: Flow<T>,
    withLoading: Boolean = true,
): Flow<T> {
    return refreshSharedFlow
        .onStart { emit(Unit) }
        .flatMapConcat { observeInternal(flow, withLoading) }
}

private fun <T> RinneResultUiState<T>.observeInternal(
    flow: Flow<T>,
    withLoading: Boolean = true,
) = flow
