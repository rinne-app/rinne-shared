package com.rinne.libraries.text.highlights.impl.model

internal data class RinneAnnotatedString(
    val plainText: String,
    val annotations: List<RinneAnnotation>,
)