package com.rinne.libraries.date.time.core

sealed interface RinneTimeZone {
    data object Local : RinneTimeZone
//    data class TimeZone(val offset: RinneDuration) : RinneTimeZone
}

