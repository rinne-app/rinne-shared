package com.rinne.shared.network.model.tasks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTasksSchedule(
    @SerialName("tasks") val tasks: List<NetworkTaskInfo>,
)
