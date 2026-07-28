package com.rinne.shared.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGenerateMemorizerSetAssistantNoteBody(
    @SerialName("query") val query: String,
)
