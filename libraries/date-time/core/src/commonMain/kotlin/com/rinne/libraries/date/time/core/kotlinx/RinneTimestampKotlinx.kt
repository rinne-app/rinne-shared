package com.rinne.libraries.date.time.core.kotlinx

import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal object RinneTimestampKotlinx {

    fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()

    fun parse(input: String): Long = Instant.parse(input).toEpochMilliseconds()

    fun format(epochMillis: Long): String = Instant.fromEpochMilliseconds(epochMillis).toString()

    /** The ISO day of the week (1 = Monday) and the minute of the day of [epochMillis] in [zoneId]. */
    fun weekTime(epochMillis: Long, zoneId: String): Pair<Int, Int> {
        val local = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.of(zoneId))
        return local.dayOfWeek.isoDayNumber to local.hour * MinutesPerHour + local.minute
    }

    private const val MinutesPerHour = 60
}
