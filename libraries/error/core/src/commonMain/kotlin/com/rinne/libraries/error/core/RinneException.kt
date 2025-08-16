package com.rinne.libraries.error.core

open class RinneException(
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)

fun RinneException(throwable: Throwable): RinneException {
    if (throwable is RinneException) return throwable

    return RinneException(throwable.message, throwable)
}

fun Throwable.asRinneException(): RinneException {
    printStackTrace()
    return RinneException(this)
}