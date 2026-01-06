package com.rinne.shared.network.model.deprecated

import com.rinne.libraries.date.time.core.RinneDateTime

data class DraftsInfo(
    val username: String,
    val drafts: List<DraftInfo>,
)

data class DraftInfo(
    val id: String,
    val title: String,
    val text: String,
    val dateTime: RinneDateTime,
)