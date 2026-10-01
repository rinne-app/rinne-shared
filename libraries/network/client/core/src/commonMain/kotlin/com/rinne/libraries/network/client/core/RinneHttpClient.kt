package com.rinne.libraries.network.client.core

import com.rinne.libraries.network.client.core.model.*

/**
 * Engine-agnostic HTTP transport. Implementations (e.g. the Ktor one) only move bytes: they don't
 * serialize bodies, add base URLs or auth — that belongs to the layers built on top of this.
 *
 * Implementations must return a response for every HTTP status and throw
 * [RinneNetworkException.NoConnection] / [RinneNetworkException.Timeout] for transport failures.
 */
interface RinneHttpClient {
    suspend fun callRequest(request: RinneHttpRequest): RinneHttpResponse
}

data class RinneHttpRequest(
    val url: RinneUrl,
    val method: RinneHttpMethod,
    val headers: RinneHttpHeaders = RinneHttpHeaders.Empty,
    val body: RinneOutgoingContent = RinneOutgoingContent.Empty,
    val attributes: RinneAttributes = RinneAttributes.Empty,
    val timeouts: RinneHttpClientConfigSettings.Timeouts? = null,
)

data class RinneHttpResponse(
    val request: RinneHttpRequest,
    val status: RinneHttpStatusCode? = null,
    val headers: RinneHttpHeaders = RinneHttpHeaders.Empty,
    val body: RinneIncomingContent = RinneIncomingContent.Empty,
    val requestTime: Long? = null,
    val responseTime: Long? = null,
)

val RinneHttpResponse.isSuccessful: Boolean
    get() = status?.isSuccess() == true

sealed interface RinneUrl {
    data class Address(val url: String) : RinneUrl
    data class Advanced(
        val protocol: RinneUrlProtocol?,
        val host: String,
        val specifiedPort: Int,
        val pathSegments: List<String>,
        val parameters: RinneParameters,
        val fragment: String,
        val user: String?,
        val password: String?,
        val trailingQuery: Boolean,
    ) : RinneUrl
}
