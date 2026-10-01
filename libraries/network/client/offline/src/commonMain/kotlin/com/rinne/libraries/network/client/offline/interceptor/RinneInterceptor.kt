package com.rinne.libraries.network.client.offline.interceptor

import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpRequest
import com.rinne.libraries.network.client.core.RinneHttpResponse

/**
 * Engine-agnostic replacement for Ktor plugins (base URL, headers, auth, logging). Each interceptor
 * may rewrite the request, short-circuit with its own response, or observe the result.
 */
fun interface RinneInterceptor {
    suspend fun intercept(chain: Chain): RinneHttpResponse

    interface Chain {
        val request: RinneHttpRequest

        suspend fun proceed(request: RinneHttpRequest): RinneHttpResponse
    }
}

internal class RinneInterceptorChain(
    private val interceptors: List<RinneInterceptor>,
    private val index: Int,
    override val request: RinneHttpRequest,
    private val transport: RinneHttpClient,
) : RinneInterceptor.Chain {

    override suspend fun proceed(request: RinneHttpRequest): RinneHttpResponse {
        val interceptor = interceptors.getOrNull(index) ?: return transport.callRequest(request)
        return interceptor.intercept(RinneInterceptorChain(interceptors, index + 1, request, transport))
    }
}
