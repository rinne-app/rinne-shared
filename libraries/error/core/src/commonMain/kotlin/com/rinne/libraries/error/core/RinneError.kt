package com.rinne.libraries.error.core

/**
 * An error the user can be shown: every feature's sealed error type and [CommonError] implement it.
 *
 * Errors are values (`Either<FeatureError, T>`), not exceptions. They reach the screen as they are;
 * which texts they show is decided by a shared texts mapping per type and resolved by each UI.
 */
interface RinneError

/**
 * A feature error that only wraps a [CommonError] (`NotesError.Common(Offline)`). Presentation unwraps
 * it, so a wrapped failure is shown exactly like the bare one.
 */
interface WrapsCommonError : RinneError {
    val error: CommonError
}

/** Failures every feature can hit. Feature errors wrap them in their `Common` variant. */
sealed interface CommonError : RinneError {
    /** No network connection. */
    data object Offline : CommonError

    /** The server answered with a server error or can't be reached; [status] is the HTTP status, if any. */
    data class ServerUnavailable(val status: Int?) : CommonError

    /** The session ended (signed out elsewhere, refresh token revoked or expired). */
    data object SessionExpired : CommonError

    /** The device ran out of space: changes are not saved. */
    data object StorageFull : CommonError

    /** The local database file can't be read or written. */
    data object StorageUnavailable : CommonError

    /** A bug: an exception nobody expected. Its cause is logged, never shown. */
    data object Unexpected : CommonError
}

/** The [CommonError] inside a [WrapsCommonError], or this error itself. */
fun RinneError.unwrapCommon(): RinneError = when (this) {
    is WrapsCommonError -> error
    else -> this
}
