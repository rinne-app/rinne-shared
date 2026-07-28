package com.rinne.shared.network.model.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageDetails(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("cover") val cover: String,
    @SerialName("type") val type: NetworkPageType,
)

@Serializable
data class NetworkGeneratePageBody(
    @SerialName("prompt") val prompt: String? = null,
    @SerialName("type") val type: NetworkPageType? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("comment") val comment: String? = null,
)
