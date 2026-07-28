package com.rinne.libraries.network.client.core.model

sealed interface RinneCharset {
    object UTF_8 : RinneCharset
    object ISO_8859_1 : RinneCharset
}