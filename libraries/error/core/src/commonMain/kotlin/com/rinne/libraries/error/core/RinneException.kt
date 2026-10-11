package com.rinne.libraries.error.core

/** Wraps the cause of a bug with context for the logs. Never shown to the user. */
class RinneException(message: String, cause: Throwable? = null) : Exception(message, cause)
