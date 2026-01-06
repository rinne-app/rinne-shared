package com.rinne.shared.network.model.deprecated

import com.rinne.libraries.date.time.core.RinneDateTime

data class Discussion(
    val id: String,
    val comments: List<DiscussionComment>,
)

data class DiscussionComment(
    val id: String,
    val authorInfo: ProfileInfo,
    val text: String,
    val rating: String,
    val dateTime: RinneDateTime,
    val answers: List<DiscussionComment>,
)