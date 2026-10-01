package com.rinne.libraries.error.compose.result.new

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import com.rinne.libraries.error.core.result.MutableRinneResult
import com.rinne.libraries.error.core.result.RinneResult
import com.rinne.libraries.error.core.result.observer.RinneResultObserver

@Composable
fun <T> rememberRinneResultUiState(
    result: RinneResult<T>,
    loggerTag: String? = null,
): RinneResultUiState<T> {
    return RinneResultUiStateImpl.remember(result, loggerTag)
}

@Composable
fun <T, R> rememberRinneResultUiStateMapper(
    result: RinneResultUiState<T>,
    transformer: RinneResultUiStateTransformer<T, R>,
): RinneResultUiState<R> {
    return remember(result, transformer) { result.map(transformer) }
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

/**
 * Bridges [observer]'s [RinneResultObserver.stateFlow] into a retained [MutableRinneResult] so the
 * last known value survives this composable leaving and re-entering composition — e.g. navigating
 * to a detail screen and back.
 *
 * The [LaunchedEffect] re-subscribes on every fresh entry into composition, rather than starting the
 * subscription once inside [retain]'s calculation: a `rememberCoroutineScope()`-backed subscription
 * started only there is cancelled the first time this leaves composition, and since `retain` never
 * re-runs that calculation for a value it already has, the subscription was never restarted — the
 * state froze at whatever it last was. (That was the bug behind a screen's data silently going stale
 * after visiting a detail screen and coming back — folder expansion on the Notes home was one
 * symptom, since the whole tree stopped updating there, not just the expanded folder.)
 */
@Composable
fun <T> rememberRinneResultUiState(
    observer: RinneResultObserver<T>,
    loggerTag: String? = null,
): RinneResultUiState<T> {
    val result = retain { MutableRinneResult<T>(observer.stateFlow.value) }

    LaunchedEffect(observer, result) {
        observer.stateFlow.collect { result.setState(it) }
    }

    return rememberRinneResultUiState(result, loggerTag)
}

@Composable
fun <T, R> rememberRinneResultUiState(
    observer: RinneResultObserver<T>,
    loggerTag: String? = null,
    transformer: RinneResultUiStateTransformer<T, R>
): RinneResultUiState<R> {
    val result = retain { MutableRinneResult<T>(observer.stateFlow.value) }

    LaunchedEffect(observer, result) {
        observer.stateFlow.collect { result.setState(it) }
    }

    return rememberRinneResultUiState(result = result, transformer = transformer, loggerTag = loggerTag)
}
