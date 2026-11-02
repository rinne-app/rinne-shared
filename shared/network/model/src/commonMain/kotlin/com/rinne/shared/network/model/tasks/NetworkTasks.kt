package com.rinne.shared.network.model.tasks

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/**
 * A task as synced. Writable fields: `title`, `description` (plain text), `folderId` (the task folder
 * it is in; `null` = Unsorted; a sub-task's is ignored: it lives in its parent's), `parentId` (the
 * task it is a sub-task of; sub-tasks are one level deep), `status` (a built-in status key of
 * [NetworkTaskStatus.BuiltIn] or a custom status's id), `position` (its place in the user's manual
 * order: a key compared as text, `null` = never arranged), `onHome` (pinned to Home), `noteIds` (the
 * notes attached to it), `priority` (one of [Priorities], `null` = none), `doneAt` (when it reached a
 * status that counts as done), `archivedAt` (when it was archived; `null` = not archived) and
 * `blockedBy` (the tasks it waits for; unknown ids are ignored). Deleting a task deletes its sub-tasks.
 */
@Serializable
data class NetworkTask(
    val id: String,
    val title: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val description: String = "",
    val folderId: String? = null,
    val parentId: String? = null,
    val status: String = NetworkTaskStatus.BuiltIn.ToDo,
    val position: String? = null,
    val onHome: Boolean = false,
    val noteIds: List<String> = emptyList(),
    val references: List<String> = emptyList(),
    val dueDate: String? = null,
    val priority: String? = null,
    val doneAt: RinneTimestamp? = null,
    val archivedAt: RinneTimestamp? = null,
    val blockedBy: List<String> = emptyList(),
) {
    companion object {
        const val SyncType = "task"
    }

    /** The priority levels, highest first. */
    object Priorities {
        const val Highest = "highest"
        const val High = "high"
        const val Medium = "medium"
        const val Low = "low"
        const val Lowest = "lowest"

        val All: List<String> = listOf(Highest, High, Medium, Low, Lowest)
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Title = SyncField("title", String.serializer())
        val Description = SyncField("description", String.serializer())
        val FolderId = SyncField("folderId", String.serializer().nullable)
        val ParentId = SyncField("parentId", String.serializer().nullable)
        val Status = SyncField("status", String.serializer())
        val Position = SyncField("position", String.serializer().nullable)
        val OnHome = SyncField("onHome", Boolean.serializer())
        val NoteIds = SyncField("noteIds", ListSerializer(String.serializer()))

        val DueDate = SyncField("dueDate", String.serializer().nullable)
        val References = SyncField("references", ListSerializer(String.serializer()))
        val Priority = SyncField("priority", String.serializer().nullable)
        val DoneAt = SyncField("doneAt", RinneTimestamp.serializer().nullable)
        val ArchivedAt = SyncField("archivedAt", RinneTimestamp.serializer().nullable)
        val BlockedBy = SyncField("blockedBy", ListSerializer(String.serializer()))

        val All: List<SyncField<*>> =
            listOf(Title, Description, FolderId, ParentId, Status, Position, OnHome, NoteIds, References, DueDate, Priority, DoneAt, ArchivedAt, BlockedBy)
    }
}

/**
 * A task folder as synced. Writable fields: `name`, `parentId` (the folder it is in; `null` = top
 * level), `pinned` (pinned in Tasks), `onHome` (pinned to Home), `position` (as a task's), `statuses`
 * (the statuses its tasks use, in the order of its sections and columns: built-in keys and custom
 * status ids; To do and Done are always among them), `autoArchiveDays` (done tasks are archived this
 * many days after they were done; one of [AutoArchiveDays], `0` = never) and `unblockAt` (the status a
 * blocker must reach to unblock the folder's tasks: one of [UnblockAt]; `null` = the account's choice).
 * Deleting a folder moves its tasks and subfolders one level up.
 */
@Serializable
data class NetworkTaskFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val parentId: String? = null,
    val pinned: Boolean = false,
    val onHome: Boolean = false,
    val position: String? = null,
    val statuses: List<String> = NetworkTaskStatus.BuiltIn.Defaults,
    val autoArchiveDays: Int = AutoArchiveDays.Default,
    val unblockAt: String? = null,
) {
    companion object {
        const val SyncType = "task_folder"
    }

    /** The choices of `autoArchiveDays`. */
    object AutoArchiveDays {
        const val Never = 0
        const val Default = 7

        val All: List<Int> = listOf(Never, 1, 7, 30)
    }

    /** The choices of `unblockAt`: built-in status keys. */
    object UnblockAt {
        val All: List<String> =
            listOf(NetworkTaskStatus.BuiltIn.InProgress, NetworkTaskStatus.BuiltIn.Review, NetworkTaskStatus.BuiltIn.Done)
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val ParentId = SyncField("parentId", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val OnHome = SyncField("onHome", Boolean.serializer())
        val Position = SyncField("position", String.serializer().nullable)
        val Statuses = SyncField("statuses", ListSerializer(String.serializer()))
        val AutoArchiveDays = SyncField("autoArchiveDays", Int.serializer())
        val UnblockAt = SyncField("unblockAt", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, ParentId, Pinned, OnHome, Position, Statuses, AutoArchiveDays, UnblockAt)
    }
}

/**
 * A status the user made, as synced. Writable fields: `name`, `color` (one of [Colors]),
 * `countsAsDone` (its tasks don't count as open). The built-in statuses ([BuiltIn]) are part of the
 * apps, not synced; a task or folder names them by their key.
 */
@Serializable
data class NetworkTaskStatus(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val color: String = Colors.Primary,
    val countsAsDone: Boolean = false,
) {
    companion object {
        const val SyncType = "task_status"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Color = SyncField("color", String.serializer())
        val CountsAsDone = SyncField("countsAsDone", Boolean.serializer())

        val All: List<SyncField<*>> = listOf(Name, Color, CountsAsDone)
    }

    /** The keys of the built-in statuses. */
    object BuiltIn {
        const val ToDo = "todo"
        const val InProgress = "in_progress"
        const val Review = "review"
        const val Blocked = "blocked"
        const val Done = "done"
        const val Backlog = "backlog"
        const val Idea = "idea"

        val All: List<String> = listOf(ToDo, InProgress, Review, Blocked, Done, Backlog, Idea)

        /** What a new folder uses. */
        val Defaults: List<String> = listOf(ToDo, InProgress, Blocked, Done, Backlog)
    }

    /** The colours a status can have (the dots of the design). */
    object Colors {
        const val Ring = "ring"
        const val Primary = "primary"
        const val Review = "review"
        const val Error = "error"
        const val Tertiary = "tertiary"
        const val Secondary = "secondary"
        const val Muted = "muted"

        val All: List<String> = listOf(Ring, Primary, Review, Error, Tertiary, Secondary, Muted)
    }
}

/**
 * A saved filter of task lists, as synced. Writable fields: `name`, `folderId` (the folder it is
 * offered in; `null` = every list), `priorities` (keys of [NetworkTask.Priorities] and [NoPriority];
 * empty = any), `tagIds` (empty = any), `tagMatch` (one of [TagMatches]: a task needs all of the tags or
 * any of them) and `position` (as a task's).
 */
@Serializable
data class NetworkTaskFilter(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val folderId: String? = null,
    val priorities: List<String> = emptyList(),
    val tagIds: List<String> = emptyList(),
    val tagMatch: String = TagMatches.All,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "task_filter"

        /** In `priorities`: tasks without a priority. */
        const val NoPriority = "none"
    }

    object TagMatches {
        const val All = "all"
        const val Any = "any"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val FolderId = SyncField("folderId", String.serializer().nullable)
        val Priorities = SyncField("priorities", ListSerializer(String.serializer()))
        val TagIds = SyncField("tagIds", ListSerializer(String.serializer()))
        val TagMatch = SyncField("tagMatch", String.serializer())
        val Position = SyncField("position", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, FolderId, Priorities, TagIds, TagMatch, Position)
    }
}
