package com.rinne.libraries.network.client.ktor

import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpClientConfig
import com.rinne.libraries.network.client.core.RinneHttpRequest
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneUrl
import com.rinne.libraries.network.client.core.model.RinneIncomingContent
import com.rinne.libraries.network.client.ktor.extensions.asKtor
import com.rinne.libraries.network.client.ktor.extensions.asRinne
import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.DEFAULT_PORT
import io.ktor.http.charset
import io.ktor.http.contentType
import io.ktor.http.isTextType
import io.ktor.util.putAll
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.readText
import io.ktor.utils.io.core.writeFully

class RinneKtorHttpClient(private val config: RinneHttpClientConfig) : RinneHttpClient {

    val httpClient by lazy {
        HttpClient {}
    }

    override suspend fun callRequest(request: RinneHttpRequest): RinneHttpResponse {
        val response = httpClient.request {

            when (val rinneUrl = request.url) {
                is RinneUrl.Address -> url(rinneUrl.url)
                is RinneUrl.Advanced -> url {
                    rinneUrl.protocol?.let { protocol = it.asKtor() }
                    host = rinneUrl.host
                    port = rinneUrl.specifiedPort.takeIf { it != 0 } ?: DEFAULT_PORT
                    pathSegments = rinneUrl.pathSegments
                    parameters.appendAll(rinneUrl.parameters.asKtor())
                    fragment = rinneUrl.fragment
                    user = rinneUrl.user
                    password = rinneUrl.password
                    trailingQuery = rinneUrl.trailingQuery
                }
            }

            method = request.method.asKtor()
            headers.appendAll(request.headers.asKtor())
            setBody(request.body.asKtor())
            attributes.putAll(request.attributes.asKtor())
        }

        val bodyBytes = runCatching { response.bodyAsBytes() }.getOrNull()

        return RinneHttpResponse(
            request = request,
            status = response.status.asRinne(),
            headers = response.headers.asRinne(),
            body = response.asRinneBody(bodyBytes),
            requestTime = response.requestTime.timestamp,
            responseTime = response.responseTime.timestamp
        )
    }
}

private fun HttpResponse.asRinneBody(bytes: ByteArray?): RinneIncomingContent {
    if (bytes == null || bytes.isEmpty()) return RinneIncomingContent.Empty

    val contentType = contentType()
    val rinneContentType = contentType?.withoutParameters()?.asRinne()

    return if (contentType?.isTextType() == true) {
        val bodyCharset = charset() ?: Charsets.UTF_8
        val text = buildPacket { writeFully(bytes) }.readText(bodyCharset)
        RinneIncomingContent.Text(
            text = text,
            charset = bodyCharset.asRinne(),
            contentType = rinneContentType
        )
    } else {
        RinneIncomingContent.Bytes(
            bytes = bytes,
            contentType = rinneContentType
        )
    }
}
