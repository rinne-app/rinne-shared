package com.rinne.shared.network.model.courses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NetworkCourseSectionInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("progress_status") val progressStatus: NetworkCourseSectionProgressStatus = NetworkCourseSectionProgressStatus(),
)

@Serializable
data class NetworkCourseSection(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("course_info") val courseInfo: NetworkCourseInfo? = null,
    @SerialName("progress_status") val progressStatus: NetworkCourseSectionProgressStatus = NetworkCourseSectionProgressStatus(),
    @SerialName("content_info") val contentInfo: List<NetworkCourseSectionContentInfo> = emptyList(),
)

@Serializable
data class NetworkCourseSectionContentInfo(
    @SerialName("theme") val theme: NetworkCourseThemeInfo,
)

@Serializable
data class NetworkCourseSectionProgressStatus(
    @SerialName("type") val type: NetworkCourseSectionProgressStatusType = NetworkCourseSectionProgressStatusType.NONE,
)

enum class NetworkCourseSectionProgressStatusType {
    NONE, COMPLETED, IN_PROGRESS,
}

fun NetworkCourseSection.asInfo() = NetworkCourseSectionInfo(
    id = id,
    name = name,
    progressStatus = progressStatus,
)