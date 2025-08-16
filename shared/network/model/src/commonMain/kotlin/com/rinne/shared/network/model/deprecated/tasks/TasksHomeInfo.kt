package com.rinne.shared.network.model.deprecated.tasks

import com.rinne.shared.network.model.deprecated.ProfileInfo

data class TasksHomeInfo(
    val owner: ProfileInfo,
    val folders: List<TasksFolderInfo>,
)

data class TasksFolderInfo(
    val id: String,
    val tasks: List<TaskInfo>,
    val type: TasksFolderType,
)

sealed interface TasksFolderType {
    data object Unsorted : TasksFolderType
    data class Default(val name: String) : TasksFolderType
}


data class TaskInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val done: Boolean,
    val priority: Int,
)