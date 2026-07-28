package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneUrlProtocol
import io.ktor.http.URLProtocol

internal fun RinneUrlProtocol.asKtor() = when (this) {
    is RinneUrlProtocol.Custom -> URLProtocol(name, defaultPort)
    RinneUrlProtocol.HTTP -> URLProtocol.HTTP
    RinneUrlProtocol.HTTPS -> URLProtocol.HTTPS
    RinneUrlProtocol.SOCKS -> URLProtocol.SOCKS
    RinneUrlProtocol.WS -> URLProtocol.WS
    RinneUrlProtocol.WSS -> URLProtocol.WSS
}

internal fun URLProtocol.asRinne() = when (this) {
    URLProtocol.HTTP -> RinneUrlProtocol.HTTP
    URLProtocol.HTTPS -> RinneUrlProtocol.HTTPS
    URLProtocol.SOCKS -> RinneUrlProtocol.SOCKS
    URLProtocol.WS -> RinneUrlProtocol.WS
    URLProtocol.WSS -> RinneUrlProtocol.WSS
    else -> RinneUrlProtocol.Custom(name, defaultPort)
}
