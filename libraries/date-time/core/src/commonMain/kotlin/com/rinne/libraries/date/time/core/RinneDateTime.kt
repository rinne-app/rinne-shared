package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneDateTimeKotlinx
import kotlinx.datetime.LocalDateTime

interface RinneInstant : Comparable<RinneInstant> {
    val timeZone: RinneTimeZone
    val epochMillis: Long

    override fun compareTo(other: RinneInstant): Int {
        return other.epochMillis.toInt() //TODO review
    }
}

interface RinneDateTime : RinneTime, RinneDate {
    val time: RinneTime
    val date: RinneDate

    override fun plus(value: Long, unit: RinneDateTimeUnit.TimeUnit): RinneDateTime
    override fun plus(value: Long, unit: RinneDateTimeUnit.DateUnit): RinneDateTime
    fun plus(value: Long, unit: RinneDateTimeUnit): RinneDateTime

    companion object : RinneDateTimeProvider by RinneDateTimeConfig.dateTimeProvider
}

fun RinneDateTime.Companion.from(localDateTime: LocalDateTime): RinneDateTime = RinneDateTimeKotlinx(localDateTime)


interface RinneTime : RinneInstant {
    val hour: Int
    val minute: Int
    val second: Int
    val nanosecond: Int

    fun plus(value: Long, unit: RinneDateTimeUnit.TimeUnit): RinneTime

    companion object : RinneTimeProvider by RinneDateTimeConfig.timeProvider
}

interface RinneDate : RinneInstant {
    val year: Int
    val month: RinneMonth
    val day: Int

    fun plus(value: Long, unit: RinneDateTimeUnit.DateUnit): RinneDate

    companion object : RinneDateProvider by RinneDateTimeConfig.dateProvider
}


fun RinneTime.copy(
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanosecond: Int = this.nanosecond,
    timeZone: RinneTimeZone = this.timeZone,
): RinneTime = RinneTime.of(hour, minute, second, nanosecond, timeZone)


fun RinneDateTime.copy(
    hour: Int = this.hour,
    minute: Int = this.minute,
    second: Int = this.second,
    nanosecond: Int = this.nanosecond,

    year: Int = this.year,
    month: RinneMonth = this.month,
    day: Int = this.day,
    timeZone: RinneTimeZone = this.timeZone,
): RinneDateTime = RinneDateTime.of(
    year = year,
    month = month,
    day = day,
    hour = hour,
    minute = minute,
    second = second,
    nanosecond = nanosecond,
    timeZone = timeZone
)

fun RinneDateTime.plusMinutes(minutes: Long) = plus(minutes, RinneDateTimeUnit.TimeUnit.Minute)
fun RinneDateTime.plusMinutes(minutes: Int) = plusMinutes(minutes.toLong())
fun RinneDateTime.plusDays(days: Long) = plus(days, RinneDateTimeUnit.DateUnit.Day)
fun RinneDateTime.plusDays(days: Int) = plusDays(days.toLong())
fun RinneDateTime.plus(duration: RinneDuration) =
    plus(duration.inWholeNanoseconds, RinneDateTimeUnit.TimeUnit.Nanosecond)

fun RinneDate.copy(
    year: Int = this.year,
    month: RinneMonth = this.month,
    day: Int = this.day,
    timeZone: RinneTimeZone = this.timeZone,
): RinneDate = RinneDate.of(year = year, month = month, day = day, timeZone = timeZone)


fun RinneDateTime.truncatedTo(unit: RinneDateTimeUnit): RinneDateTime {
    var month = month
    var day = day
    var hour = hour
    var minute = minute
    var second = second
    var nanosecond = nanosecond


    if (RinneDateTimeUnit.DateUnit.Month in unit.includes) month = RinneMonth.first
    if (RinneDateTimeUnit.DateUnit.Day in unit.includes) day = 1
    if (RinneDateTimeUnit.TimeUnit.Hour in unit.includes) hour = 0
    if (RinneDateTimeUnit.TimeUnit.Minute in unit.includes) minute = 0
    if (RinneDateTimeUnit.TimeUnit.Second in unit.includes) second = 0
    if (RinneDateTimeUnit.TimeUnit.Nanosecond in unit.includes) nanosecond = 0

    return copy(
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = nanosecond,
        day = day,
        month = month,
    )
}

