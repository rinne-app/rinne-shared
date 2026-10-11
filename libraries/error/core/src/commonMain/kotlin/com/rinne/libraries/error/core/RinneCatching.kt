package com.rinne.libraries.error.core

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.rinne.libraries.logger.core.RootRinneLogger
import com.rinne.libraries.logger.core.extensions.e
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

/**
 * Runs [block] and turns an unexpected exception into [CommonError.Unexpected], logging its cause.
 *
 * The one place that catches exceptions for the app: [CancellationException] is always rethrown (so
 * cancellation keeps working), and so are JVM-fatal errors. Operational failures that are *expected*
 * — disk full, offline — are mapped to their own errors by the layer that knows them, before this.
 */
inline fun <T> rinneCatching(context: String = "rinneCatching", block: () -> T): Either<CommonError, T> =
    try {
        block().right()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        throwable.rethrowIfFatal()
        logUnexpected(context, throwable)
        CommonError.Unexpected.left()
    }

/**
 * Like [rinneCatching] for a block that already returns an `Either`: a `Left` stays as it is, an
 * exception becomes [CommonError.Unexpected].
 */
inline fun <T> rinneCatchingEither(
    context: String = "rinneCatchingEither",
    block: () -> Either<RinneError, T>,
): Either<RinneError, T> = rinneCatching(context, block).fold({ it.left() }, { it })

/** Emits each value as `Right`; an exception ends the flow with one `Left(Unexpected)` (logged). */
fun <T> Flow<T>.catchAsEither(context: String = "catchAsEither"): Flow<Either<RinneError, T>> =
    map<T, Either<RinneError, T>> { it.right() }
        .catch { throwable ->
            throwable.rethrowIfFatal()
            logUnexpected(context, throwable)
            emit(CommonError.Unexpected.left())
        }

/** For a flow of `Either`s: values pass through, an exception becomes one `Left(Unexpected)` (logged). */
fun <E : RinneError, T> Flow<Either<E, T>>.catchUnexpected(
    context: String = "catchUnexpected",
): Flow<Either<RinneError, T>> =
    map<Either<E, T>, Either<RinneError, T>> { it }.catch { throwable ->
        throwable.rethrowIfFatal()
        logUnexpected(context, throwable)
        emit(CommonError.Unexpected.left())
    }

@PublishedApi
internal fun logUnexpected(context: String, throwable: Throwable) {
    RootRinneLogger.default.e(message = "Unexpected exception in $context", throwable = throwable)
}

/** Rethrows what must never be caught: cancellation and errors the runtime can't recover from. */
@PublishedApi
internal fun Throwable.rethrowIfFatal() {
    if (this is CancellationException) throw this
    if (isFatal()) throw this
}

internal expect fun Throwable.isFatal(): Boolean
