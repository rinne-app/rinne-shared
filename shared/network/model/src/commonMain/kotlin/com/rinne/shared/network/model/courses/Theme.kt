package com.rinne.shared.network.model.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCourseThemeInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("progress_status") val completionStatus: NetworkCourseThemeProgressStatus = NetworkCourseThemeProgressStatus(),
)

@Serializable
data class NetworkCourseThemeDetails(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("content") val content: String,
    @SerialName("section_info") val sectionInfo: NetworkCourseSectionInfo? = null,
    @SerialName("progress_status") val progressStatus: NetworkCourseThemeProgressStatus = NetworkCourseThemeProgressStatus(),
)


@Serializable
data class NetworkCourseThemeProgressStatus(
    @SerialName("type") val type: NetworkCourseThemeProgressStatusType = NetworkCourseThemeProgressStatusType.NONE,
)

enum class NetworkCourseThemeProgressStatusType {
    NONE, COMPLETED, IN_PROGRESS,
}

fun NetworkCourseThemeDetails.asInfo() = NetworkCourseThemeInfo(
    id = id,
    name = name,
    completionStatus = progressStatus,
)