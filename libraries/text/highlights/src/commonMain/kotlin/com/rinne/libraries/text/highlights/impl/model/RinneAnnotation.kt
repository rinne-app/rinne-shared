package com.rinne.libraries.text.highlights.impl.model

internal data class RinneAnnotation(
    val value: String,
    val start: Int,
    val end: Int,
    val attribute: RinneAnnotationAttribute
)
