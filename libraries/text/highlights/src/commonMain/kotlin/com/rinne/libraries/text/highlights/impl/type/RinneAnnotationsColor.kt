package com.rinne.libraries.text.highlights.impl.type

internal enum class RinneAnnotationsColor(val key: String) {
    ERROR("error"),
    PRIMARY("primary");

    companion object {
        fun find(key: String) = entries.firstOrNull { it.key == key }
    }
}