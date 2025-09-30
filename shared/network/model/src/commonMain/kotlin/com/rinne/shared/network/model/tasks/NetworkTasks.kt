package com.rinne.shared.network.model.tasks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTasksGroup(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: NetworkTasksGroupType,
    @SerialName("tasks") val tasks: List<NetworkTaskInfo>,
    @SerialName("availableStatuses") val availableStatuses: List<NetworkTasksStatusInfo>,
)

enum class NetworkTasksGroupType {
    UNSORTED, DEFAULT
}

@Serializable
data class NetworkTasksGroupInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)

fun NetworkTasksGroup.asInfo() = NetworkTasksGroupInfo(id = id, name = name)

@Serializable
data class NetworkTask(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("status") val status: NetworkTasksStatusInfo,
    @SerialName("availableStatuses") val availableStatuses: List<NetworkTasksStatusInfo>,
    @SerialName("group") val group: NetworkTasksGroupInfo,
)

@Serializable
data class NetworkEditTask(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("statusId") val statusId: String?,
)

@Serializable
data class NetworkTaskInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("status") val status: NetworkTasksStatusInfo,
)

@Serializable
data class NetworkTaskStatus(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: NetworkTaskStatusType,
    @SerialName("group") val group: NetworkTasksGroupInfo,
) {
    companion object {
        fun default(group: NetworkTasksGroupInfo) = listOf(
            NetworkTaskStatus("TODO", "To-do", NetworkTaskStatusType.TODO, group),
            NetworkTaskStatus("BLOCKED", "Blocked", NetworkTaskStatusType.BLOCKED, group),
            NetworkTaskStatus("DONE", "Done", NetworkTaskStatusType.DONE, group),
        )
    }
}


@Serializable
data class NetworkTasksStatusInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("type") val type: NetworkTaskStatusType,
)

enum class NetworkTaskStatusType {
    TODO, BLOCKED, DONE, CUSTOM;

    companion object
}

fun NetworkTaskStatusType.Companion.valueOfOrCustom(name: String): NetworkTaskStatusType {
    return NetworkTaskStatusType.entries.firstOrNull { it.name == name } ?: NetworkTaskStatusType.CUSTOM
}