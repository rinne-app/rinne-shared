package com.rinne.libraries.network.client.core

import com.rinne.libraries.network.client.core.model.RinneContentType
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.logger.core.RinneLogger


data class RinneHttpClientConfig(
    val defaultRequest: RinneHttpClientConfigSettings.DefaultRequest? = null,
    val timeouts: RinneHttpClientConfigSettings.Timeouts? = null,
    val logging: RinneHttpClientConfigSettings.Logging? = null,
)

sealed interface RinneHttpClientConfigSettings {
    data class DefaultRequest(
        val url: String,
        val contentType: RinneContentType = RinneContentType.Application.Json,
        val accept: RinneContentType = RinneContentType.Application.Json,
        val headers: RinneHttpHeaders = RinneHttpHeaders.Empty,
    ) : RinneHttpClientConfigSettings

    data class Timeouts(
        val socketTimeoutMillis: Long? = null,
        val requestTimeoutMillis: Long? = null,
        val connectTimeoutMillis: Long? = null,
    ) : RinneHttpClientConfigSettings

    data class Logging(
        val logger: RinneLogger,
    ) : RinneHttpClientConfigSettings
}
