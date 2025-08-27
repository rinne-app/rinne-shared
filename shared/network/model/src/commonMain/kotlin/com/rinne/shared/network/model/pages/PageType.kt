package com.rinne.shared.network.model.pages

enum class NetworkPageType {
    DEFAULT, PLACE, PERSON, LANGUAGE;

    companion object {
        val default = DEFAULT
    }
}

fun NetworkPageType.Companion.valueOfOrDefault(name: String): NetworkPageType {
    return NetworkPageType.entries.firstOrNull { it.name == name } ?: NetworkPageType.default
}