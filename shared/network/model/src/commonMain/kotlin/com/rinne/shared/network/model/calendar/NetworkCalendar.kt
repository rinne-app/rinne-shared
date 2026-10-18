package com.rinne.shared.network.model.calendar

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

@Serializable
data class NetworkCalendar(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val color: String = Colors.Primary,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "calendar"
        const val Personal = "personal"
    }
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Color = SyncField("color", String.serializer())
        val Position = SyncField("position", String.serializer().nullable)
        val All: List<SyncField<*>> = listOf(Name, Color, Position)
    }
    object Colors {
        const val Primary = "primary"
        const val Tertiary = "tertiary"
        const val Secondary = "secondary"
        const val Review = "review"
        const val Error = "error"
        const val Muted = "muted"
        val All: List<String> = listOf(Primary, Tertiary, Secondary, Review, Error, Muted)
    }
}

@Serializable
sealed interface NetworkEventTime {
    @Serializable
    @SerialName("timed")
    data class Timed(val start: RinneTimestamp, val minutes: Int, val zone: String) : NetworkEventTime

    @Serializable
    @SerialName("all_day")
    data class AllDay(val date: String, val days: Int = 1) : NetworkEventTime
}

@Serializable
data class NetworkCalendarEvent(
    val id: String,
    val title: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val time: NetworkEventTime,
    val calendarId: String = NetworkCalendar.Personal,
    val description: String = "",
    val taskId: String? = null,
    val reminderMinutes: Int? = null,
    val references: List<String> = emptyList(),
    val templateId: String? = null,
    val applicationId: String? = null,
) {
    companion object { const val SyncType = "calendar_event" }
    object Fields {
        val Title = SyncField("title", String.serializer())
        val Time = SyncField("time", NetworkEventTime.serializer())
        val CalendarId = SyncField("calendarId", String.serializer())
        val Description = SyncField("description", String.serializer())
        val TaskId = SyncField("taskId", String.serializer().nullable)
        val ReminderMinutes = SyncField("reminderMinutes", Int.serializer().nullable)
        val References = SyncField("references", ListSerializer(String.serializer()))
        val TemplateId = SyncField("templateId", String.serializer().nullable)
        val ApplicationId = SyncField("applicationId", String.serializer().nullable)
        val All: List<SyncField<*>> = listOf(Title, Time, CalendarId, Description, TaskId, ReminderMinutes, References, TemplateId, ApplicationId)
    }
}

@Serializable
data class NetworkCalendarTemplate(
    val id: String,
    val name: String,
    val length: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
) {
    companion object { const val SyncType = "calendar_template" }
    // Length is create-only: its descriptor is read on create and rejected on update by the adapter.
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Length = SyncField("length", String.serializer())
        val All: List<SyncField<*>> = listOf(Name, Length)
    }
    object Lengths {
        const val Day = "day"
        const val Week = "week"
        val All = listOf(Day, Week)
    }
}

@Serializable
data class NetworkCalendarTemplateEvent(
    val id: String,
    val templateId: String,
    val title: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val startMinute: Int,
    val minutes: Int,
    val weekday: Int? = null,
    val calendarId: String = NetworkCalendar.Personal,
    val taskId: String? = null,
    val reminderMinutes: Int? = null,
) {
    companion object { const val SyncType = "calendar_template_event" }
    // TemplateId is create-only, with the same adapter rule as a template's Length.
    object Fields {
        val TemplateId = SyncField("templateId", String.serializer())
        val Title = SyncField("title", String.serializer())
        val Weekday = SyncField("weekday", Int.serializer().nullable)
        val StartMinute = SyncField("startMinute", Int.serializer())
        val Minutes = SyncField("minutes", Int.serializer())
        val CalendarId = SyncField("calendarId", String.serializer())
        val TaskId = SyncField("taskId", String.serializer().nullable)
        val ReminderMinutes = SyncField("reminderMinutes", Int.serializer().nullable)
        val All: List<SyncField<*>> = listOf(TemplateId, Title, Weekday, StartMinute, Minutes, CalendarId, TaskId, ReminderMinutes)
    }
}
