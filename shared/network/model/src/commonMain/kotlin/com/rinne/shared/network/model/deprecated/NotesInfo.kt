package com.rinne.shared.network.model.deprecated

import com.rinne.libraries.date.time.core.RinneDateTime

data class NotesInfo(
    val username: String,
    val notes: List<NoteInfo>,
)

data class NoteInfo(
    val id: String,
    val title: String,
    val text: String,
    val dateTime: RinneDateTime,
)
