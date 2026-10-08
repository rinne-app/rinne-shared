package com.rinne.libraries.date.time.core

import com.rinne.libraries.date.time.core.kotlinx.RinneTimestampKotlinx
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * An absolute point in time with millisecond precision, independent of any time zone.
 *
 * Use it for moments that must survive storage and transport unchanged (created/updated
 * timestamps, token expirations). [RinneDateTime] is a wall-clock value in [RinneTimeZone.Local]
 * and is ambiguous around DST transitions, so it must not be used for that.
 *
 * Serialized as an ISO-8601 UTC string, e.g. `2026-10-08T12:30:00.123Z`.
 */
@JvmInline
@Serializable(with = RinneTimestampIsoSerializer::class)
value class RinneTimestamp(val epochMillis: Long) : Comparable<RinneTimestamp> {

    override fun compareTo(other: RinneTimestamp): Int = epochMillis.compareTo(other.epochMillis)

    operator fun plus(duration: RinneDuration): RinneTimestamp =
        RinneTimestamp(epochMillis + duration.inWholeMilliseconds)

    operator fun minus(duration: RinneDuration): RinneTimestamp =
        RinneTimestamp(epochMillis - duration.inWholeMilliseconds)

    /** ISO-8601 representation in UTC, e.g. `2026-10-08T12:30:00.123Z`. */
    fun toIsoString(): String = RinneTimestampKotlinx.format(epochMillis)

    override fun toString(): String = toIsoString()

    companion object {
        /** Parses an ISO-8601 instant with an offset (`Z` or `+02:00`), throwing on malformed input. */
        fun parseIso(input: String): RinneTimestamp = RinneTimestamp(RinneTimestampKotlinx.parse(input))

        /** Like [parseIso], but returns `null` on malformed input. */
        fun parseIsoOrNull(input: String): RinneTimestamp? =
            runCatching { parseIso(input) }.getOrNull()
    }
}
