package com.rinne.shared.network.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCalendarSchedule(
    @SerialName("events") val events: List<NetworkCalendarEvent>,
    @SerialName("taskEvents") val taskEvents: List<NetworkTaskCalendarEvent> = emptyList(),
    @SerialName("config") val config: NetworkCalendarConfig,
    @SerialName("templateApplicationDays")
    val templateApplicationDays: List<NetworkCalendarTemplateApplicationDay> = emptyList(),
)

@Serializable
data class NetworkTaskCalendarEvent(
    @SerialName("taskId") val taskId: String,
    @SerialName("title") val title: String,
    @SerialName("startEpochMillis") val startEpochMillis: Long,
    @SerialName("durationSeconds") val durationSeconds: Long? = null,
)

@Serializable
data class NetworkCalendarConfig(
    @SerialName("defaultDurationSeconds") val defaultDurationSeconds: Long,
)

@Serializable
data class NetworkCalendarEvent(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("startEpochMillis") val startEpochMillis: Long,
    @SerialName("durationSeconds") val durationSeconds: Long,
)

@Serializable
data class NetworkEditCalendarEvent(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("startEpochMillis") val startEpochMillis: Long,
    @SerialName("durationSeconds") val durationSeconds: Long,
)
