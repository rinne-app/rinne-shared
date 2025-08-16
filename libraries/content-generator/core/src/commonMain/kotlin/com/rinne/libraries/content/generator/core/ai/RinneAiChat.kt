package com.rinne.libraries.content.generator.core.ai

import com.rinne.libraries.content.generator.core.impl.ai.RinneAiChatImpl

interface RinneAiChat {

    val defaultConfig: RinneAiConfig

    fun updateDefaultConfig(config: RinneAiConfig)

    suspend fun sendMessage(message: RinneAiChatMessage): RinneAiMessageAnswer

    companion object {
        fun Default(): RinneAiChat = RinneAiChatImpl()
    }
}

sealed interface RinneAiModel {
    sealed interface ChatGptModel : RinneAiModel

    data object ChatGpt5mini : ChatGptModel
    data object ChatGpt4oMini : ChatGptModel
    data object ChatGpt4o : ChatGptModel

    companion object {
        val default = ChatGpt5mini
    }
}

data class RinneAiChatMessage(
    val current: RinneAiMessage,
    val history: List<RinneAiMessage> = emptyList(),
    val chatId: String? = null,
    val customConfig: RinneAiConfig? = null,
)

data class RinneAiMessage(
    val instructions: String? = null,
    val message: String,
)

data class RinneAiConfig(
    val model: RinneAiModel,
)


data class RinneAiMessageAnswer(
    val content: String,
)
