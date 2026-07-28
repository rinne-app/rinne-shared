package com.rinne.shared.network.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkCalendarTemplateType {
    @SerialName("day")
    DAY,

    @SerialName("week")
    WEEK,
}

@Serializable
data class NetworkCalendarTemplates(
    @SerialName("templates") val templates: List<NetworkCalendarTemplateInfo>,
)

@Serializable
data class NetworkCalendarTemplateInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("type") val type: NetworkCalendarTemplateType,
)

@Serializable
data class NetworkCalendarTemplateDetails(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("type") val type: NetworkCalendarTemplateType,
    // Single entry for DAY templates, seven entries for WEEK templates.
    @SerialName("days") val days: List<NetworkCalendarTemplateDay>,
)

@Serializable
data class NetworkCalendarTemplateDay(
    @SerialName("dayTemplateId") val dayTemplateId: String,
    // 0..6 for WEEK template days, null for DAY templates. Mapping of indices
    // to weekdays is a UI concern.
    @SerialName("weekday") val weekday: Int? = null,
    @SerialName("events") val events: List<NetworkCalendarTemplateEvent>,
)

@Serializable
data class NetworkCalendarTemplateEvent(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("startSecondsOfDay") val startSecondsOfDay: Long,
    @SerialName("durationSeconds") val durationSeconds: Long,
)

@Serializable
data class NetworkCreateCalendarTemplate(
    @SerialName("title") val title: String,
    @SerialName("type") val type: NetworkCalendarTemplateType,
)

@Serializable
data class NetworkEditCalendarTemplate(
    @SerialName("title") val title: String,
)

@Serializable
data class NetworkEditCalendarTemplateEvent(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("startSecondsOfDay") val startSecondsOfDay: Long,
    @SerialName("durationSeconds") val durationSeconds: Long,
)

@Serializable
data class NetworkApplyCalendarTemplate(
    @SerialName("days") val days: List<NetworkApplyCalendarTemplateDay>,
    @SerialName("timeZoneId") val timeZoneId: String,
    @SerialName("applyChanges") val applyChanges: Boolean,
)

@Serializable
data class NetworkApplyCalendarTemplateDay(
    @SerialName("epochDay") val epochDay: Long,
    // Which weekday of a WEEK template lands on this date; null for DAY templates.
    @SerialName("weekday") val weekday: Int? = null,
)

@Serializable
data class NetworkEditCalendarTemplateApplication(
    @SerialName("applyChanges") val applyChanges: Boolean,
)

@Serializable
data class NetworkCalendarTemplateApplicationDay(
    @SerialName("id") val id: String,
    @SerialName("applicationId") val applicationId: String,
    @SerialName("templateId") val templateId: String,
    @SerialName("templateTitle") val templateTitle: String,
    @SerialName("epochDay") val epochDay: Long,
    @SerialName("applyChanges") val applyChanges: Boolean,
)
