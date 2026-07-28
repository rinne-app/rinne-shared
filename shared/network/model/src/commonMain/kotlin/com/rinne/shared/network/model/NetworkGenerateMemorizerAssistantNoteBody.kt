package com.rinne.shared.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGenerateMemorizerAssistantNoteBody(
    @SerialName("memorizerId") val memorizerId: String,
    @SerialName("query") val query: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
)
