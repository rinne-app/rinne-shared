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

/** Case-insensitive lookup of the first value of the header [name]. */
operator fun RinneHttpHeaders.get(name: String): String? =
    headers.firstOrNull { it.name.equals(name, ignoreCase = true) }?.value

/** Returns a copy with [name] set to [value], replacing any existing values of that header. */
fun RinneHttpHeaders.with(name: String, value: String): RinneHttpHeaders =
    RinneHttpHeaders.Custom(without(name).headers + RinneHttpHeader.Default(name, value))

/** Returns a copy without the header [name]. */
fun RinneHttpHeaders.without(name: String): RinneHttpHeaders {
    val remaining = headers.filterNot { it.name.equals(name, ignoreCase = true) }
    return if (remaining.isEmpty()) RinneHttpHeaders.Empty else RinneHttpHeaders.Custom(remaining)
}

operator fun RinneHttpHeaders.plus(other: RinneHttpHeaders): RinneHttpHeaders =
    other.headers.fold(this) { result, header -> result.with(header.name, header.value) }
