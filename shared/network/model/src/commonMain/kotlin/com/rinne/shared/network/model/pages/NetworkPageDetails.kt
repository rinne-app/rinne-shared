package com.rinne.shared.network.model.pages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageDetails(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("type") val type: NetworkPageTypeInfo,
    @SerialName("cover_url") val coverUrl: String,
)

@Serializable
data class NetworkPageTypeInfo(
    @SerialName("type") val type: NetworkPageType,
)