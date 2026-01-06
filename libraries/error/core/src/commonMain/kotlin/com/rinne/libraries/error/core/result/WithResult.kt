package com.rinne.libraries.error.core.result

import com.rinne.libraries.error.core.asRinneException
import com.rinne.libraries.logger.core.RootRinneLogger
import com.rinne.libraries.logger.core.extensions.e
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

suspend fun <T> withResult(
    result: MutableRinneResult<T> = MutableRinneResult(),
    withLoading: Boolean = true,
    block: suspend () -> T,
): RinneResult<T> {
    if (result is MutableRinneResultWithTag)
        return withResult(result = result, block = block)

    if (withLoading) result.setLoading()

    runCatching { block() }
        .onSuccess(result::set)
        .onFailure { RootRinneLogger.default.e(throwable = it) }
        .onFailure(result::set)

    return result
}

@OptIn(ExperimentalUuidApi::class)
suspend fun <T> withResult(
    result: MutableRinneResultWithTag<T>,
    tag: String = Uuid.random().toString(),
    withLoading: Boolean = true,
    block: suspend () -> T,
): RinneResult<T> {
    if (withLoading) result.setLoading(tag = tag)

    runCatching { block() }
        .onSuccess { result.setState(RinneResultState.Success(it), tag) }
        .onFailure {
            RootRinneLogger.default.e(throwable = it)
            result.setState(RinneResultState.Error(it.asRinneException()), tag)
        }

    return result
}