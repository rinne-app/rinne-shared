package com.rinne.libraries.date.time.core.kotlinx

import com.rinne.libraries.date.time.core.*
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object RinneDateProviderKotlinx : RinneDateProvider {
    override fun of(
        year: Int,
        month: RinneMonth,
        day: Int,
        timeZone: RinneTimeZone,
    ): RinneDate {
        return RinneDateKotlinx(
            year = year,
            month = month,
            day = day,
            timeZone = timeZone,
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun now(): RinneDate {
        return RinneDateTimeProviderKotlinx.now().date
    }

    override fun parse(input: String): RinneDate {
        return RinneDateKotlinx(LocalDate.parse(input))
    }
}

object RinneTimeProviderKotlinx : RinneTimeProvider {
    override fun of(
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        timeZone: RinneTimeZone,
    ): RinneTime {
        return RinneTimeKotlinx(
            hour = hour,
            minute = minute,
            second = second,
            nanosecond = nanosecond,
            timeZone = timeZone,
        )
    }

    override fun now(): RinneTime {
        return RinneDateTimeProviderKotlinx.now().time
    }

    override fun parse(input: String): RinneTime {
        return RinneTimeKotlinx(LocalTime.parse(input))
    }
}

object RinneDateTimeProviderKotlinx : RinneDateTimeProvider {

    override fun of(
        year: Int,
        month: RinneMonth,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        timeZone: RinneTimeZone,
    ): RinneDateTime {
        return RinneDateTimeKotlinx(
            year = year,
            month = month,
            day = day,
            hour = hour,
            minute = minute,
            second = second,
            nanosecond = nanosecond,
            timeZone = timeZone,
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun now(): RinneDateTime {
        return RinneDateTimeKotlinx(
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    @OptIn(ExperimentalTime::class)
    override fun parse(input: String): RinneDateTime {
        return RinneDateTimeKotlinx(Instant.parse(input).toLocalDateTime(TimeZone.currentSystemDefault()))
    }

    override fun parse(epochMillis: Long): RinneDateTime {
        return RinneDateTimeKotlinx(
            Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }
}