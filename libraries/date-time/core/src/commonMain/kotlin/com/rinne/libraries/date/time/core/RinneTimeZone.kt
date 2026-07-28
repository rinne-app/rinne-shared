package com.rinne.libraries.date.time.core

import kotlinx.datetime.TimeZone

sealed interface RinneTimeZone {
    data object Local : RinneTimeZone
//    data class TimeZone(val offset: RinneDuration) : RinneTimeZone
}

// IANA time zone identifier (e.g. "Europe/Amsterdam") of this zone
val RinneTimeZone.id: String
    get() = when (this) {
        RinneTimeZone.Local -> TimeZone.currentSystemDefault().id
    }
