package com.rinne.libraries.network.client.core.model

data class RinneAttributeKey<T : Any>(val name: String) {
    init {
        require(name.isNotBlank()) { "Name can't be blank" }
    }

    override fun toString(): String = "AttributeKey: $name"
}

interface RinneAttributes : Map<RinneAttributeKey<*>, Any>