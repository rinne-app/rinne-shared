package com.rinne.shared.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGenerateMemorizerCardFieldBody(
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("prompt") val prompt: String,
)
