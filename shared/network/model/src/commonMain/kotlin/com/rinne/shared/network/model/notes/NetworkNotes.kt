package com.rinne.shared.network.model.notes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkNoteSource {
    USER,
    AI_ASSISTANT,
}

@Serializable
data class NetworkNoteInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("text") val text: String,
    @SerialName("dateTime") val dateTime: String,
    @SerialName("source") val source: NetworkNoteSource = NetworkNoteSource.USER,
)
