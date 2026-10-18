package com.rinne.libraries.date.time.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RinneCalendarDateTest {
    @Test fun `dates have civil value equality and work as map keys`() {
        val date = RinneDate.parse("2026-10-18")
        assertEquals(date, RinneDate.parse("2026-10-18"))
        assertEquals(3, mapOf(date to 3)[RinneDate.parse("2026-10-18")])
    }
    @Test fun `ISO dates validate and round trip including leap days`() {
        assertEquals("2024-02-29", RinneDate.parseIso("2024-02-29").toIsoString())
        assertNull(RinneDate.parseIsoOrNull("2026-02-29"))
        assertNull(RinneDate.parseIsoOrNull("tomorrow"))
        assertEquals(0L, RinneDate.parseIso("1970-01-01").epochDay)
        assertEquals(-1L, RinneDate.parseIso("1969-12-31").epochDay)
    }
    @Test fun `calendar arithmetic clamps month and year boundaries`() {
        val leap = RinneDate.parse("2024-02-29")
        assertEquals("2025-02-28", leap.plusYears(1).toIsoString())
        assertEquals("2024-02-29", RinneDate.parse("2024-01-31").plusMonths(1).toIsoString())
        assertEquals("2024-03-01", leap.plusDays(1).toIsoString())
        assertEquals(29, leap.lengthOfMonth)
        assertEquals(2L, leap.daysUntil(leap.plusDays(2)))
        assertTrue(leap < leap.plusDays(1))
    }
    @Test fun `week starts and week years cover year boundaries`() {
        val date = RinneDate.parse("2021-01-01")
        assertEquals(RinneWeekday.Friday, date.dayOfWeek)
        assertEquals(53, date.isoWeekNumber)
        assertEquals("2020-12-28", date.startOfWeek(RinneWeekday.Monday).toIsoString())
        assertEquals("2020-12-27", date.startOfWeek(RinneWeekday.Sunday).toIsoString())
        assertEquals("2020-12-26", date.startOfWeek(RinneWeekday.Saturday).toIsoString())
        assertEquals(1, date.weekNumber(RinneWeekday.Sunday))
        assertEquals(1, date.weekNumber(RinneWeekday.Saturday))
        assertEquals(1, RinneDate.parse("2021-01-04").isoWeekNumber)
    }
    @Test fun `named zones validate and local conversions preserve milliseconds`() {
        assertNull(RinneTimeZone.Named.ofOrNull("Nowhere/Invalid"))
        assertFailsWith<IllegalArgumentException> { RinneTimeZone.Named.of("Nowhere/Invalid") }
        assertNotNull(RinneTimeZone.current())
        val zone = RinneTimeZone.Named.of("Europe/Berlin")
        val instant = RinneTimestamp.parseIso("2026-10-18T12:30:00.123Z")
        val local = instant.toLocal(zone)
        assertEquals(14, local.hour)
        assertEquals(zone, local.timeZone)
        assertEquals(instant, local.toTimestamp(zone))
        assertEquals("2026-10-17T22:00:00Z", local.date.atStartOfDay(zone).toIsoString())
    }
    @Test fun `DST gaps move forward and overlaps choose the earlier offset`() {
        val zone = RinneTimeZone.Named.of("Europe/Berlin")
        val gap = RinneDateTime.of(2026, RinneMonth.of(3), 29, 2, 30)
        val overlap = RinneDateTime.of(2026, RinneMonth.of(10), 25, 2, 30)
        assertEquals("2026-03-29T01:30:00Z", gap.toTimestamp(zone).toIsoString())
        assertEquals(3, gap.toTimestamp(zone).toLocal(zone).hour)
        assertEquals("2026-10-25T00:30:00Z", overlap.toTimestamp(zone).toIsoString())
    }
}
