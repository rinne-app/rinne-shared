package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneCharset
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets

internal fun RinneCharset.asKtor() = when (this) {
    RinneCharset.UTF_8 -> Charsets.UTF_8
    RinneCharset.ISO_8859_1 -> Charsets.ISO_8859_1
}

internal fun Charset.asRinne() = when (this) {
    Charsets.UTF_8 -> RinneCharset.UTF_8
    Charsets.ISO_8859_1 -> RinneCharset.ISO_8859_1
    else -> RinneCharset.UTF_8
}
