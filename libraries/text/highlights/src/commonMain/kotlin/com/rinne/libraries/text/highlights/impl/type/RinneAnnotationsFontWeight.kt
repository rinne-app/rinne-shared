package com.rinne.libraries.text.highlights.impl.type

internal enum class RinneAnnotationsFontWeight(val key: String) {
    MEDIUM("medium"),
    SEMIBOLD("semibold"),
    BOLD("bold");

    companion object {
        fun find(key: String) = entries.firstOrNull { it.key == key }
    }
}