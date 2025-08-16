package com.rinne.shared.network.model.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCourseDetails(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("cover_url") val coverUrl: String,
    @SerialName("access_status") val accessStatus: NetworkCourseAccessStatus = NetworkCourseAccessStatus(),
    @SerialName("progress_status") val progressStatus: NetworkCourseProgressStatus = NetworkCourseProgressStatus(),
    @SerialName("sections") val sections: List<NetworkCourseSection> = emptyList(),
)

@Serializable
data class NetworkCourseInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("cover_url") val coverUrl: String,
    @SerialName("access_status") val accessStatus: NetworkCourseAccessStatus = NetworkCourseAccessStatus(),
    @SerialName("progress_status") val progressStatus: NetworkCourseProgressStatus = NetworkCourseProgressStatus(),
)


@Serializable
data class NetworkCourseAccessStatus(
    @SerialName("type") val type: NetworkCourseAccessStatusType = NetworkCourseAccessStatusType.NONE,
)

enum class NetworkCourseAccessStatusType {
    NONE, USER_COURSE
}

@Serializable
data class NetworkCourseProgressStatus(
    @SerialName("type") val type: NetworkCourseProgressStatusType = NetworkCourseProgressStatusType.AVAILABLE,
)

enum class NetworkCourseProgressStatusType {
    AVAILABLE, COMPLETED, IN_PROGRESS
}

fun NetworkCourseDetails.asInfo() = NetworkCourseInfo(
    id = id,
    name = name,
    coverUrl = coverUrl,
    accessStatus = accessStatus,
    progressStatus = progressStatus,
)