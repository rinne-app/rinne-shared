package com.rinne.libraries.date.time.core

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
sealed interface RinneTimeZone {
    @Serializable
    data object Local : RinneTimeZone

    @Serializable
    data class Named(val value: String) : RinneTimeZone {
        init { TimeZone.of(value) }

        companion object {
            fun of(id: String): Named = Named(id)
            fun ofOrNull(id: String): Named? = try { of(id) } catch (_: IllegalArgumentException) { null }
        }
    }

    companion object {
        fun current(): Named = Named.of(TimeZone.currentSystemDefault().id)
    }
}

val RinneTimeZone.id: String
    get() = when (this) {
        is RinneTimeZone.Named -> value
        RinneTimeZone.Local -> TimeZone.currentSystemDefault().id
    }
