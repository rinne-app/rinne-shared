package com.rinne.libraries.network.client.offline.request

import com.rinne.libraries.network.client.core.RinneHttpClientConfigSettings
import com.rinne.libraries.network.client.core.RinneHttpRequest
import com.rinne.libraries.network.client.core.RinneUrl
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import com.rinne.libraries.network.client.offline.asMethodName
import com.rinne.libraries.network.client.offline.mutation.replaceTempIds

/** A request as the caller described it: relative path, before base URL/auth interceptors run. */
internal data class RinneRequestSpec(
    val method: RinneHttpMethod,
    val path: String,
    val parameters: Map<String, List<String>> = emptyMap(),
    val headers: RinneHttpHeaders = RinneHttpHeaders.Empty,
    val body: RinneOutgoingContent = RinneOutgoingContent.Empty,
    val timeouts: RinneHttpClientConfigSettings.Timeouts? = null,
) {
    fun toHttpRequest() = RinneHttpRequest(
        url = RinneUrl.Address(path + parameters.asQueryString()),
        method = method,
        headers = headers,
        body = body,
        timeouts = timeouts,
    )

    fun withResolvedTempIds(mappings: Map<String, String>) = copy(path = path.replaceTempIds(mappings))

    /** Identity of the request for caching: scope + method + path + order-independent query. */
    fun cacheKey(scope: String) =
        "$scope ${method.asMethodName()} ${path.trim('/')}${parameters.asQueryString()}"
}

internal fun Map<String, List<String>>.asQueryString(): String {
    if (isEmpty()) return ""
    return entries
        .sortedBy { it.key }
        .flatMap { (name, values) -> values.map { "${name.encodeQueryComponent()}=${it.encodeQueryComponent()}" } }
        .joinToString(separator = "&", prefix = "?")
}

private const val UNRESERVED = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~"
private const val HEX = "0123456789ABCDEF"

internal fun String.encodeQueryComponent(): String = buildString {
    for (byte in this@encodeQueryComponent.encodeToByteArray()) {
        val char = byte.toInt().toChar()
        if (byte >= 0 && char in UNRESERVED) {
            append(char)
        } else {
            val value = byte.toInt() and 0xFF
            append('%').append(HEX[value shr 4]).append(HEX[value and 0x0F])
        }
    }
}
