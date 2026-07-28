package com.rinne.libraries.network.client.core

import com.rinne.libraries.network.client.core.model.*
import kotlinx.coroutines.Job

interface RinneHttpClient {
    suspend fun callRequest(request: RinneHttpRequest): RinneHttpResponse
}

data class RinneHttpRequest(
    val url: RinneUrl,
    val method: RinneHttpMethod,
    val headers: RinneHttpHeaders,
    val body: RinneOutgoingContent,
    val executionContext: Job,
    val attributes: RinneAttributes,
)

data class RinneHttpResponse(
    val request: RinneHttpRequest,
    val status: RinneHttpStatusCode? = null,
    val headers: RinneHttpHeaders = RinneHttpHeaders.Empty,
    val body: RinneIncomingContent = RinneIncomingContent.Empty,
    val requestTime: Long? = null,
    val responseTime: Long? = null,
)

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
