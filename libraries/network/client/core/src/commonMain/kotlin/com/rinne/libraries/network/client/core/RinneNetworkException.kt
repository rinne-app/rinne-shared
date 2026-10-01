package com.rinne.libraries.network.client.core

import com.rinne.libraries.network.client.core.model.asTextOrNull

/**
 * Engine-independent network failures, so callers (retry logic, offline queues) can classify errors
 * without knowing which HTTP engine produced them.
 */
sealed class RinneNetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    /** The request never reached the server: no network, DNS failure, refused connection. */
    class NoConnection(cause: Throwable? = null) :
        RinneNetworkException("No connection: ${cause?.message.orEmpty()}", cause)

    /** The server did not answer in time. The request may or may not have been applied. */
    class Timeout(cause: Throwable? = null) :
        RinneNetworkException("Request timed out: ${cause?.message.orEmpty()}", cause)

    /** The server answered with a non-successful status. */
    class Http(val response: RinneHttpResponse) : RinneNetworkException(
        "HTTP ${response.status?.value} ${response.status?.description.orEmpty()}: " +
            response.body.asTextOrNull().orEmpty().take(MAX_BODY_IN_MESSAGE)
    ) {
        val statusCode: Int? get() = response.status?.value
    }

    private companion object {
        const val MAX_BODY_IN_MESSAGE = 500
    }
}
