package com.rinne.shared.network.model.deprecated.ai

data class AiChat(
    val id: String,
    val title: String,
    val messages: List<AiChatMessage>,
)

data class AiChatMessage(
    val id: String,
    val content: String,
    val author: AiChatMessageAuthorType,
)

enum class AiChatMessageAuthorType {
    USER, AI
}