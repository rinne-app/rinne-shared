package com.rinne.shared.network.model.digest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDigestChannel(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("sources") val sources: List<String>,
)

@Serializable
data class NetworkDigestChannelInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
)

@Serializable
data class NetworkEditDigestChannel(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("sources") val sources: List<String>,
)

@Serializable
enum class NetworkDigestStatus {
    GENERATING, READY, FAILED
}

@Serializable
data class NetworkDigestInfo(
    @SerialName("id") val id: String,
    @SerialName("rangeStartEpochMillis") val rangeStartEpochMillis: Long,
    @SerialName("rangeEndEpochMillis") val rangeEndEpochMillis: Long,
    @SerialName("summary") val summary: List<String>,
    @SerialName("status") val status: NetworkDigestStatus,
    @SerialName("errorMessage") val errorMessage: String? = null,
)

@Serializable
data class NetworkDigest(
    @SerialName("id") val id: String,
    @SerialName("channel") val channel: NetworkDigestChannelInfo,
    @SerialName("rangeStartEpochMillis") val rangeStartEpochMillis: Long,
    @SerialName("rangeEndEpochMillis") val rangeEndEpochMillis: Long,
    @SerialName("promptDetails") val promptDetails: String? = null,
    @SerialName("summary") val summary: List<String>,
    @SerialName("status") val status: NetworkDigestStatus,
    @SerialName("errorMessage") val errorMessage: String? = null,
    @SerialName("articles") val articles: List<NetworkDigestArticleInfo>,
)

@Serializable
data class NetworkCreateDigest(
    @SerialName("rangeStartEpochMillis") val rangeStartEpochMillis: Long,
    @SerialName("rangeEndEpochMillis") val rangeEndEpochMillis: Long,
    @SerialName("promptDetails") val promptDetails: String? = null,
)

@Serializable
data class NetworkDigestArticleInfo(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("tags") val tags: List<String>,
    @SerialName("excerpt") val excerpt: String,
)

@Serializable
data class NetworkDigestArticle(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("eventDateEpochMillis") val eventDateEpochMillis: Long,
    @SerialName("tags") val tags: List<String>,
    @SerialName("content") val content: String,
    @SerialName("sources") val sources: List<NetworkDigestArticleSource>,
)

@Serializable
data class NetworkDigestArticleSource(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String,
    @SerialName("quote") val quote: String? = null,
)
