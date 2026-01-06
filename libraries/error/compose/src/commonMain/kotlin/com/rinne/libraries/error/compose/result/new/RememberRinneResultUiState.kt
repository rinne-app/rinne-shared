package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.retain.retain
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.observer.RinneResultObserver
import com.rinne.libraries.error.core.result.observer.observeAsRinneResult
import kotlinx.coroutines.CoroutineScope

@Composable
fun <T> rememberRinneResultUiState(
    result: RinneResult<T>,
    loggerTag: String? = null,
): RinneResultUiState<T> {
    return RinneResultUiStateImpl.remember(result, loggerTag)
}

@Composable
fun <T, R> rememberRinneResultUiState(
    result: RinneResult<T>,
    loggerTag: String? = null,
    transformer: RinneResultUiStateTransformer<T, R>,
): RinneResultUiState<R> {
    val state = RinneResultUiStateImpl.remember(result, loggerTag)

    return retain(state) { state.map(transformer) }
}

@Composable
fun <T> rememberRinneResultUiState(
    observer: RinneResultObserver<T>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    loggerTag: String? = null,
): RinneResultUiState<T> {
    val result = retain(coroutineScope) { observer.observeAsRinneResult(coroutineScope) }

    return rememberRinneResultUiState(result, loggerTag)
}

@Composable
fun <T, R> rememberRinneResultUiState(
    observer: RinneResultObserver<T>,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    loggerTag: String? = null,
    transformer: RinneResultUiStateTransformer<T, R>
): RinneResultUiState<R> {
    val result = retain(coroutineScope) { observer.observeAsRinneResult(coroutineScope) }

    return rememberRinneResultUiState(result = result, transformer = transformer, loggerTag = loggerTag)
}
