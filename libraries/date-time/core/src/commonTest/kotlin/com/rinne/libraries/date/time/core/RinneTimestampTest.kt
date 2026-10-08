package com.rinne.libraries.date.time.core

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RinneTimestampTest {

    @Test
    fun `formats as ISO-8601 in UTC`() {
        assertEquals("2026-10-08T12:30:00.123Z", RinneTimestamp(1_791_462_600_123).toIsoString())
    }

    @Test
    fun `parses ISO-8601 with an offset into the same instant`() {
        val utc = RinneTimestamp.parseIso("2026-10-08T12:30:00Z")
        val offset = RinneTimestamp.parseIso("2026-10-08T14:30:00+02:00")

        assertEquals(utc, offset)
    }

    @Test
    fun `round-trips through its ISO string`() {
        val timestamp = RinneTimestamp(1_791_462_600_123)

        assertEquals(timestamp, RinneTimestamp.parseIso(timestamp.toIsoString()))
    }

    @Test
    fun `parseIsoOrNull returns null for malformed input`() {
        assertNull(RinneTimestamp.parseIsoOrNull("2026-10-08 12:30"))
    }

    @Test
    fun `adds and subtracts durations`() {
        val timestamp = RinneTimestamp(1_000)

        assertEquals(RinneTimestamp(61_000), timestamp + 1.minutes)
        assertEquals(RinneTimestamp(500), timestamp - 500.milliseconds)
    }

    @Test
    fun `orders by the moment in time`() {
        assertTrue(RinneTimestamp(1) < RinneTimestamp(2))
    }

    @Test
    fun `serializes as an ISO string`() {
        val timestamp = RinneTimestamp(1_791_462_600_123)
        val json = Json.encodeToString(RinneTimestamp.serializer(), timestamp)

        assertEquals("\"2026-10-08T12:30:00.123Z\"", json)
        assertEquals(timestamp, Json.decodeFromString(RinneTimestamp.serializer(), json))
    }

    @Test
    fun `rejects a malformed serialized value`() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString(RinneTimestamp.serializer(), "\"yesterday\"")
        }
    }

    @Test
    fun `fixed clock returns its timestamp`() {
        val clock = RinneClock { RinneTimestamp(42) }

        assertEquals(RinneTimestamp(42), clock.now())
    }
}
