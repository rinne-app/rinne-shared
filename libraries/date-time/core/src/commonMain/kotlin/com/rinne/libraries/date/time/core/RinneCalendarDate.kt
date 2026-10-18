package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneDateKotlinx
import com.rinne.libraries.date.time.core.kotlinx.RinneDateTimeKotlinx
import com.rinne.libraries.date.time.core.kotlinx.asKotlinx
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Instant

@Serializable
enum class RinneWeekday(val isoNumber: Int) {
    Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6), Sunday(7)
}

fun RinneDate.Companion.parseIso(input: String): RinneDate = RinneDateKotlinx(LocalDate.parse(input))
fun RinneDate.Companion.parseIsoOrNull(input: String): RinneDate? =
    try { parseIso(input) } catch (_: IllegalArgumentException) { null }

fun RinneDate.toIsoString(): String = asLocalDate().toString()
val RinneDate.epochDay: Long get() = asLocalDate().toEpochDays()
val RinneDate.dayOfWeek: RinneWeekday get() = RinneWeekday.entries[asLocalDate().dayOfWeek.isoDayNumber - 1]
val RinneDate.lengthOfMonth: Int
    get() = (asLocalDate().plus(1, DateTimeUnit.MONTH).let { LocalDate(it.year, it.month, 1) }.toEpochDays() -
        LocalDate(year, month.number, 1).toEpochDays()).toInt()
val RinneDate.isoWeekNumber: Int get() = weekNumber(RinneWeekday.Monday)
fun RinneDate.plusDays(days: Int): RinneDate = RinneDateKotlinx(asLocalDate().plus(days, DateTimeUnit.DAY))
fun RinneDate.plusMonths(months: Int): RinneDate = RinneDateKotlinx(asLocalDate().plus(months, DateTimeUnit.MONTH))
fun RinneDate.plusYears(years: Int): RinneDate = RinneDateKotlinx(asLocalDate().plus(years, DateTimeUnit.YEAR))
fun RinneDate.daysUntil(other: RinneDate): Long = other.epochDay - epochDay
fun RinneDate.startOfWeek(first: RinneWeekday): RinneDate = plusDays(-(dayOfWeek.isoNumber - first.isoNumber).mod(7))

/** ISO weeks for Monday; Sunday/Saturday weeks use the week containing January 1 as week 1. */
fun RinneDate.weekNumber(first: RinneWeekday): Int {
    val minimumDays = when (first) { RinneWeekday.Monday -> 4; else -> 1 }
    fun firstWeek(year: Int): RinneDate = RinneDate.parseIso("0000-01-01").copy(year = year, day = minimumDays).startOfWeek(first)
    val start = startOfWeek(first)
    val thisYear = firstWeek(year)
    val weekYearStart = when {
        start.epochDay < thisYear.epochDay -> firstWeek(year - 1)
        start.epochDay >= firstWeek(year + 1).epochDay -> firstWeek(year + 1)
        else -> thisYear
    }
    return (weekYearStart.daysUntil(start) / 7).toInt() + 1
}

fun RinneTimestamp.toLocal(zone: RinneTimeZone = RinneTimeZone.Local): RinneDateTime {
    val local = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(zone.asKotlinx())
    return RinneDateTimeKotlinx(local.year, RinneMonth.of(local.monthNumber), local.day, local.hour,
        local.minute, local.second, local.nanosecond, zone)
}

/** DST gaps advance by the gap's length; overlaps use the earlier offset. */
fun RinneDateTime.toTimestamp(zone: RinneTimeZone = timeZone): RinneTimestamp = RinneTimestamp(
    LocalDateTime(year, month.number, day, hour, minute, second, nanosecond).toInstant(zone.asKotlinx()).toEpochMilliseconds(),
)
fun RinneDate.atStartOfDay(zone: RinneTimeZone = RinneTimeZone.Local): RinneTimestamp =
    RinneTimestamp(asLocalDate().atStartOfDayIn(zone.asKotlinx()).toEpochMilliseconds())

private fun RinneDate.asLocalDate(): LocalDate = LocalDate(year, month.number, day)

object RinneDateIsoSerializer : KSerializer<RinneDate> {
    override val descriptor = PrimitiveSerialDescriptor("RinneDate", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: RinneDate) = encoder.encodeString(value.toIsoString())
    override fun deserialize(decoder: Decoder): RinneDate = RinneDate.parseIso(decoder.decodeString())
}
