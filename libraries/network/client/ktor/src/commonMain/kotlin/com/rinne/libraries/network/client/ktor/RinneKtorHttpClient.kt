package com.rinne.libraries.network.client.ktor

import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpClientConfig
import com.rinne.libraries.network.client.core.RinneHttpRequest
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.core.RinneUrl
import com.rinne.libraries.network.client.core.model.RinneIncomingContent
import com.rinne.libraries.network.client.ktor.extensions.asKtor
import com.rinne.libraries.network.client.ktor.extensions.asRinne
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.pluginOrNull
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
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
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

/**
 * [RinneHttpClient] backed by Ktor. Pass an already configured [HttpClient] (engine, timeouts,
 * certificates) or build one from a [RinneHttpClientConfig]. Everything above raw transport — base
 * URL, auth, serialization, caching — is expected to live in the engine-agnostic layer on top, so
 * migrating off Ktor only means replacing this class.
 */
class RinneKtorHttpClient(private val httpClient: HttpClient) : RinneHttpClient {

    constructor(config: RinneHttpClientConfig) : this(createKtorHttpClient(config))

    override suspend fun callRequest(request: RinneHttpRequest): RinneHttpResponse {
        val response = try {
            httpClient.request { applyRinneRequest(request) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: RinneNetworkException) {
            throw e
        } catch (e: Throwable) {
            throw e.asRinneNetworkException() ?: e
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

    private fun HttpRequestBuilder.applyRinneRequest(request: RinneHttpRequest) {
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
        request.headers.headers.forEach { header(it.name, it.value) }
        setBody(request.body.asKtor())
        attributes.putAll(request.attributes.asKtor())

        val timeouts = request.timeouts
        // Ktor rejects per-request timeouts when the HttpTimeout plugin isn't installed.
        if (timeouts != null && httpClient.pluginOrNull(HttpTimeout) != null) {
            timeout {
                timeouts.requestTimeoutMillis?.let { requestTimeoutMillis = it }
                timeouts.connectTimeoutMillis?.let { connectTimeoutMillis = it }
                timeouts.socketTimeoutMillis?.let { socketTimeoutMillis = it }
            }
        }
    }
}

/** Builds a Ktor client from the engine-agnostic config, for callers without their own [HttpClient]. */
fun createKtorHttpClient(config: RinneHttpClientConfig): HttpClient = HttpClient {
    config.defaultRequest?.let { defaults ->
        defaultRequest {
            url(defaults.url)
            header(io.ktor.http.HttpHeaders.ContentType, defaults.contentType.asKtor().toString())
            header(io.ktor.http.HttpHeaders.Accept, defaults.accept.asKtor().toString())
            defaults.headers.headers.forEach { header(it.name, it.value) }
        }
    }

    install(HttpTimeout) {
        config.timeouts?.let { timeouts ->
            timeouts.requestTimeoutMillis?.let { requestTimeoutMillis = it }
            timeouts.connectTimeoutMillis?.let { connectTimeoutMillis = it }
            timeouts.socketTimeoutMillis?.let { socketTimeoutMillis = it }
        }
    }

    config.logging?.let { logging ->
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    logging.logger.i(message = message)
                }
            }
        }
    }
}

private fun Throwable.asRinneNetworkException(): RinneNetworkException? = when {
    this is HttpRequestTimeoutException ||
        this is ConnectTimeoutException ||
        this is SocketTimeoutException -> RinneNetworkException.Timeout(this)

    this is IOException || isPlatformConnectivityFailure() -> RinneNetworkException.NoConnection(this)
    else -> null
}

/** Engine failures that mean "no connection" but don't extend [IOException] on this platform. */
internal expect fun Throwable.isPlatformConnectivityFailure(): Boolean

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
