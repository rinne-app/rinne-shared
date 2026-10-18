package com.rinne.libraries.date.time.core.kotlinx

import com.rinne.libraries.date.time.core.*
import kotlinx.datetime.*
import kotlin.time.ExperimentalTime


internal class RinneDateKotlinx(
    override val year: Int,
    override val month: RinneMonth,
    override val day: Int,
    override val timeZone: RinneTimeZone = RinneTimeZone.Local,
) : RinneDate {
    constructor(date: LocalDate) : this(date.year, date.month.asRinneMonth(), date.day)

    private val dateKotlinx = LocalDate(year, month.number, day)

    @OptIn(ExperimentalTime::class)
    override val epochMillis: Long
        get() = dateKotlinx.atStartOfDayIn(timeZone.asKotlinx()).epochSeconds.seconds.inWholeMilliseconds

    override fun equals(other: Any?): Boolean = other is RinneDateKotlinx && dateKotlinx == other.dateKotlinx
    override fun hashCode(): Int = dateKotlinx.hashCode()
    override fun toString(): String = dateKotlinx.toString()
    override fun compareTo(other: RinneInstant): Int = when (other) {
        is RinneDate -> epochDay.compareTo(other.epochDay)
        else -> epochMillis.compareTo(other.epochMillis)
    }

    override fun plus(value: Long, unit: RinneDateTimeUnit.DateUnit): RinneDate {
        return RinneDateKotlinx(dateKotlinx.plus(value, unit.asKotlinx() as DateTimeUnit.DateBased))
    }

}

internal class RinneTimeKotlinx(
    override val hour: Int,
    override val minute: Int,
    override val second: Int,
    override val nanosecond: Int,
    override val timeZone: RinneTimeZone = RinneTimeZone.Local,
) : RinneTime {
    constructor(time: LocalTime) : this(time.hour, time.minute, time.second, time.nanosecond)

    private val timeKotlinx = LocalTime(hour, minute, second, nanosecond)

    @OptIn(ExperimentalTime::class)
    override val epochMillis: Long
        get() = timeKotlinx.toMillisecondOfDay().toLong()

    @OptIn(ExperimentalTime::class)
    override fun plus(value: Long, unit: RinneDateTimeUnit.TimeUnit): RinneTime {
        val dt = LocalDateTime(1970, 1, 1, hour, minute, second, nanosecond)
        // Use UTC to avoid DST shifts when calculating time addition
        val instant = dt.toInstant(TimeZone.UTC)
        val newInstant = instant.plus(value, unit.asKotlinx(), TimeZone.UTC)
        val newDt = newInstant.toLocalDateTime(TimeZone.UTC)
        return RinneTimeKotlinx(newDt.time)
    }
}

internal class RinneDateTimeKotlinx(
    override val year: Int,
    override val month: RinneMonth,
    override val day: Int,

    override val hour: Int,
    override val minute: Int,
    override val second: Int,
    override val nanosecond: Int,

    override val timeZone: RinneTimeZone = RinneTimeZone.Local,
) : RinneDateTime {
    override val time: RinneTime get() = RinneTimeKotlinx(hour, minute, second, nanosecond, timeZone)
    override val date: RinneDate get() = RinneDateKotlinx(year, month, day, timeZone)

    constructor(dateTime: LocalDateTime) : this(
        year = dateTime.year,
        month = dateTime.month.asRinneMonth(),
        day = dateTime.day,
        hour = dateTime.hour,
        minute = dateTime.minute,
        second = dateTime.second,
        nanosecond = dateTime.nanosecond,
    )


    private val dateTimeKotlinx = LocalDateTime(
        year = year,
        month = month.number,
        day = day,
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = nanosecond
    )

    @OptIn(ExperimentalTime::class)
    override val epochMillis: Long
        get() = dateTimeKotlinx.toInstant(timeZone.asKotlinx()).toEpochMilliseconds()

    @OptIn(ExperimentalTime::class)
    override fun plus(value: Long, unit: RinneDateTimeUnit): RinneDateTime {
        val tz = timeZone.asKotlinx()
        val instant = dateTimeKotlinx.toInstant(tz)
        val newInstant = instant.plus(value, unit.asKotlinx(), tz)
        val newDateTime = newInstant.toLocalDateTime(tz)
        return RinneDateTimeKotlinx(newDateTime).copy(timeZone = timeZone)
    }

    override fun plus(value: Long, unit: RinneDateTimeUnit.TimeUnit): RinneDateTime {
        return plus(value, unit as RinneDateTimeUnit)
    }

    override fun plus(value: Long, unit: RinneDateTimeUnit.DateUnit): RinneDateTime {
        return plus(value, unit as RinneDateTimeUnit)
    }

    override fun toString(): String {
        return dateTimeKotlinx.toString()
    }

    override fun hashCode(): Int {
        return dateTimeKotlinx.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RinneDateTimeKotlinx

        if (year != other.year) return false
        if (day != other.day) return false
        if (hour != other.hour) return false
        if (minute != other.minute) return false
        if (second != other.second) return false
        if (nanosecond != other.nanosecond) return false
        if (month != other.month) return false
        if (timeZone != other.timeZone) return false

        return true
    }
}

fun RinneTimeZone.asKotlinx() = when (this) {
    is RinneTimeZone.Named -> TimeZone.of(value)
    RinneTimeZone.Local -> TimeZone.currentSystemDefault()
}

fun RinneDateTimeUnit.asKotlinx(): DateTimeUnit = when (this) {
    RinneDateTimeUnit.TimeUnit.Nanosecond -> DateTimeUnit.NANOSECOND
    RinneDateTimeUnit.TimeUnit.Second -> DateTimeUnit.SECOND
    RinneDateTimeUnit.TimeUnit.Minute -> DateTimeUnit.MINUTE
    RinneDateTimeUnit.TimeUnit.Hour -> DateTimeUnit.HOUR
    RinneDateTimeUnit.DateUnit.Day -> DateTimeUnit.DAY
    RinneDateTimeUnit.DateUnit.Month -> DateTimeUnit.MONTH
    RinneDateTimeUnit.DateUnit.Year -> DateTimeUnit.YEAR
    else -> error("Unknown unit: $this")
}