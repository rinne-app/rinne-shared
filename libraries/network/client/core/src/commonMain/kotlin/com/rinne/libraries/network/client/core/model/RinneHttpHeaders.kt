package com.rinne.libraries.network.client.core.model


sealed interface RinneHttpHeader {
    val name: String
    val value: String

    data class Default(
        override val name: String,
        override val value: String
    ) : RinneHttpHeader
}

sealed interface RinneHttpHeaders {
    val headers: List<RinneHttpHeader>

    data object Empty : RinneHttpHeaders {
        override val headers: List<RinneHttpHeader> = emptyList()
    }

    data class Custom(override val headers: List<RinneHttpHeader>) : RinneHttpHeaders
}