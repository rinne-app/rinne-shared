package com.rinne.shared.network.model

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.calendar.*
import com.rinne.shared.network.model.sync.*
import com.rinne.shared.network.model.tasks.NetworkTask
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CalendarWireTest {
    private val now = RinneTimestamp(0)

    @Test fun `timed and all day events retain their time kind across JSON and sync fields`() {
        val times = listOf(NetworkEventTime.Timed(now, 60, "Europe/Berlin"), NetworkEventTime.AllDay("2026-10-18", 3))
        times.forEach { time ->
            val event = NetworkCalendarEvent("event", "Read", now, now, time)
            assertEquals(event, Json.decodeFromString(NetworkCalendarEvent.serializer(), Json.encodeToString(NetworkCalendarEvent.serializer(), event)))
            assertEquals(SyncFieldValue.Present(time), syncFields { set(NetworkCalendarEvent.Fields.Time, time) }.readSyncField(NetworkCalendarEvent.Fields.Time))
        }
        assertEquals("all_day", Json.parseToJsonElement(Json.encodeToString(NetworkEventTime.serializer(), times.last())).jsonObject["type"]?.jsonPrimitive?.content)
    }

    @Test fun `old task payloads default to no due date`() {
        val wire = """{"id":"task","title":"Read","createdAt":"1970-01-01T00:00:00Z","updatedAt":"1970-01-01T00:00:00Z"}"""
        assertNull(Json.decodeFromString(NetworkTask.serializer(), wire).dueDate)
    }

    @Test fun `omitted event options default for old clients`() {
        val wire = """{"id":"event","title":"Read","createdAt":"1970-01-01T00:00:00Z","updatedAt":"1970-01-01T00:00:00Z","time":{"type":"all_day","date":"2026-10-18"}}"""
        val event = Json.decodeFromString(NetworkCalendarEvent.serializer(), wire)
        assertEquals(NetworkCalendar.Personal, event.calendarId)
        assertEquals(NetworkEventTime.AllDay("2026-10-18"), event.time)
        assertEquals(emptyList(), event.references)
        assertNull(event.reminderMinutes)
    }

    @Test fun `nullable due dates can be explicitly cleared in sync fields`() {
        assertEquals(SyncFieldValue.Present(null), syncFields { set(NetworkTask.Fields.DueDate, null) }.readSyncField(NetworkTask.Fields.DueDate))
    }
}
