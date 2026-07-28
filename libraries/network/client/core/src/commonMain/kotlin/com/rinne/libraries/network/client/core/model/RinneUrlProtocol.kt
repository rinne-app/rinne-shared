package com.rinne.libraries.network.client.core.model


sealed interface RinneUrlProtocol {
    data class Custom(
        val name: String,
        val defaultPort: Int,
    ) : RinneUrlProtocol

    /**
     * HTTP with port 80
     */
    object HTTP : RinneUrlProtocol

    /**
     * secure HTTPS with port 443
     */
    object HTTPS : RinneUrlProtocol

    /**
     * Web socket over HTTP on port 80
     */
    object WS : RinneUrlProtocol

    /**
     * Web socket over secure HTTPS on port 443
     */
    object WSS : RinneUrlProtocol

    /**
     * Socks proxy url protocol.
     */
    object SOCKS : RinneUrlProtocol
}