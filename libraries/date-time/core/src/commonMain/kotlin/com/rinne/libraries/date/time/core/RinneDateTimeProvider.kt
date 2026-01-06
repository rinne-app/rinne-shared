package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneDateTimeProviderKotlinx

interface RinneDateTimeProvider {
    fun of(
        year: Int,
        month: RinneMonth,
        day: Int = 1,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        nanosecond: Int = 0,
        timeZone: RinneTimeZone = RinneTimeZone.Local,
    ): RinneDateTime

    fun now(): RinneDateTime
    fun parse(input: String): RinneDateTime
    fun parse(epochMillis: Long): RinneDateTime
}

interface RinneDateProvider {
    fun of(
        year: Int,
        month: RinneMonth,
        day: Int,
        timeZone: RinneTimeZone = RinneTimeZone.Local,
    ): RinneDate

    fun now(): RinneDate
    fun parse(input: String): RinneDate
}

fun RinneDateTimeProvider.of(
    time: RinneTime,
    date: RinneDate
): RinneDateTime {
    return RinneDateTimeProviderKotlinx.of(
        year = date.year,
        month = date.month,
        day = date.day,
        hour = time.hour,
        minute = time.minute,
        second = time.second,
        nanosecond = time.nanosecond
    )
}

fun RinneDateTimeProvider.of(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    second: Int,
    nanosecond: Int,
): RinneDateTime {
    return RinneDateTimeProviderKotlinx.of(
        year = year,
        month = RinneMonth.of(month),
        day = day,
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = nanosecond
    )
}

interface RinneTimeProvider {
    fun of(
        hour: Int,
        minute: Int,
        second: Int = 0,
        nanosecond: Int = 0,
        timeZone: RinneTimeZone = RinneTimeZone.Local,
    ): RinneTime

    fun now(): RinneTime
    fun parse(input: String): RinneTime
}