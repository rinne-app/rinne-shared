package com.rinne.shared.network.model.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkAiPrompt(
    @SerialName("key") val key: String,
    @SerialName("content") val content: String,
)

@Serializable
data class NetworkAiModel(
    @SerialName("key") val key: String,
    @SerialName("name") val name: String,
    @SerialName("provider") val provider: String,
)

@Serializable
data class NetworkAiCurrentModelRequest(
    @SerialName("key") val key: String,
)
