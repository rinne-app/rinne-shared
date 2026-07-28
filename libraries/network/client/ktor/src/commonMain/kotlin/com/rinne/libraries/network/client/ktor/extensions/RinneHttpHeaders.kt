package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneHttpHeader
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import io.ktor.http.Headers

internal fun RinneHttpHeaders.asKtor() = when {
    this is RinneHttpHeaders.Empty || this.headers.isEmpty() -> Headers.Empty
    else -> Headers.build {
        headers.forEach { header ->
            append(header.name, header.value)
        }
    }
}

internal fun Headers.asRinne() = when (isEmpty()) {
    true -> RinneHttpHeaders.Empty
    false -> RinneHttpHeaders.Custom(
        headers = buildList {
            forEach { name, values ->
                values.forEach { value ->
                    add(RinneHttpHeader.Default(name, value))
                }
            }
        }
    )
}
