package com.rinne.shared.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSuggestTranslationBody(
    @SerialName("text") val text: String,
)