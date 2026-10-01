package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.offline.connectivity.RinneConnectivity
import com.rinne.libraries.network.client.offline.interceptor.RinneInterceptor
import com.rinne.libraries.network.client.offline.interceptor.RinneInterceptorChain
import com.rinne.libraries.network.client.offline.request.RinneRequestSpec

/** Runs a request through the interceptors and the transport. */
internal class RinneRequestExecutor(
    private val transport: RinneHttpClient,
    private val interceptors: List<RinneInterceptor>,
    private val connectivity: RinneConnectivity,
) {
    /** Called after any response arrives: the server is reachable again. */
    var onServerReachable: () -> Unit = {}

    suspend fun execute(spec: RinneRequestSpec): RinneHttpResponse {
        // Without a network the engine would only fail after its connect timeout; fail right away
        // so stored values and queued mutations take over immediately.
        if (!connectivity.isOnline.value) throw RinneNetworkException.NoConnection()

        val request = spec.toHttpRequest()
        return RinneInterceptorChain(interceptors, 0, request, transport)
            .proceed(request)
            .also { onServerReachable() }
    }
}
