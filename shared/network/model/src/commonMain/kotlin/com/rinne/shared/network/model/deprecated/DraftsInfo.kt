package com.rinne.shared.network.model.deprecated

import kotlinx.datetime.LocalDateTime

data class DraftsInfo(
    val username: String,
    val drafts: List<DraftInfo>,
)

data class DraftInfo(
    val id: String,
    val title: String,
    val text: String,
    val dateTime: LocalDateTime,
)