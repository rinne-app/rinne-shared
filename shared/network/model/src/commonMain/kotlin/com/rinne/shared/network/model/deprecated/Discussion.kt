package com.rinne.shared.network.model.deprecated

import kotlinx.datetime.LocalDateTime

data class Discussion(
    val id: String,
    val comments: List<DiscussionComment>,
)

data class DiscussionComment(
    val id: String,
    val authorInfo: ProfileInfo,
    val text: String,
    val rating: String,
    val dateTime: LocalDateTime,
    val answers: List<DiscussionComment>,
)