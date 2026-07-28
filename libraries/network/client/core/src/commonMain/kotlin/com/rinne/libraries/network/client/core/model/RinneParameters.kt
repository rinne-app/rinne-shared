package com.rinne.libraries.network.client.core.model

data class RinneParameters(
    val entries: Map<String, List<String>> = emptyMap()
) {
    fun values(name: String): List<String> = entries[name].orEmpty()

    fun first(name: String): String? = entries[name]?.firstOrNull()

    companion object {
        val Empty: RinneParameters = RinneParameters()
    }
}
