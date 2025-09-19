package com.rinne.shared.network.model.memorizer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMemorizerDetails(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("folders") val folders: List<NetworkMemorizerFolderInfo>,
)

@Serializable
data class NetworkMemorizerFolderInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("cards") val cards: List<NetworkMemorizerCardInfo>,
)

@Serializable
data class NetworkMemorizerCardInfo(
    @SerialName("id") val id: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("notes") val notes: String?,
)