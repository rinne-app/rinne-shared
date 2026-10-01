package com.rinne.libraries.network.client.offline.interceptor

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.network.client.core.RinneUrl
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.network.client.core.model.get
import com.rinne.libraries.network.client.core.model.with
import com.rinne.libraries.network.client.offline.asMethodName

/** Prefixes relative [RinneUrl.Address] URLs with [baseUrl], read on every request so it can change at runtime. */
class RinneBaseUrlInterceptor(private val baseUrl: () -> String) : RinneInterceptor {
    override suspend fun intercept(chain: RinneInterceptor.Chain) = chain.proceed(
        when (val url = chain.request.url) {
            is RinneUrl.Address -> when (url.url.contains("://")) {
                true -> chain.request
                false -> chain.request.copy(url = RinneUrl.Address(joinUrl(baseUrl(), url.url)))
            }

            is RinneUrl.Advanced -> chain.request
        }
    )

    private fun joinUrl(base: String, path: String) = base.trimEnd('/') + "/" + path.trimStart('/')
}

/** Adds [headers] to every request unless the request already sets them. */
class RinneDefaultHeadersInterceptor(private val headers: () -> RinneHttpHeaders) : RinneInterceptor {
    override suspend fun intercept(chain: RinneInterceptor.Chain) = chain.proceed(
        chain.request.copy(
            headers = headers().headers.fold(chain.request.headers) { result, header ->
                if (result[header.name] != null) result else result.with(header.name, header.value)
            }
        )
    )
}

/**
 * Attaches the current token at send time. Tokens are never part of cached or queued requests, so a
 * mutation replayed after a token refresh uses the fresh one.
 */
class RinneAuthInterceptor(
    private val headerName: String = AUTHORIZATION,
    private val prefix: String = BEARER_PREFIX,
    private val token: suspend () -> String?,
) : RinneInterceptor {
    override suspend fun intercept(chain: RinneInterceptor.Chain) = chain.proceed(
        when (val value = token()) {
            null -> chain.request
            else -> chain.request.copy(headers = chain.request.headers.with(headerName, prefix + value))
        }
    )

    companion object {
        const val AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
    }
}

class RinneLoggingInterceptor(private val logger: RinneLogger) : RinneInterceptor {
    override suspend fun intercept(chain: RinneInterceptor.Chain) = run {
        val request = chain.request
        val url = (request.url as? RinneUrl.Address)?.url ?: request.url.toString()
        logger.i(message = "--> ${request.method.asMethodName()} $url")
        try {
            chain.proceed(request).also { response ->
                logger.i(message = "<-- ${response.status?.value} ${request.method.asMethodName()} $url")
            }
        } catch (e: Throwable) {
            logger.i(message = "<-- FAILED ${request.method.asMethodName()} $url: ${e.message}")
            throw e
        }
    }
}
