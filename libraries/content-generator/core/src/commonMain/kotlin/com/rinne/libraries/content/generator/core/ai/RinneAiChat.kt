package com.rinne.libraries.content.generator.core.ai

import com.rinne.libraries.content.generator.core.impl.ai.RinneAiChatImpl

interface RinneAiChat {

    val defaultConfig: RinneAiConfig

    fun updateDefaultConfig(config: RinneAiConfig)

    suspend fun sendMessage(message: RinneAiChatMessage): RinneAiMessageAnswer

    companion object {
        fun Default(apiKey: String): RinneAiChat = Default(RinneAiConfig(apiKey))
        fun Default(defaultConfig: RinneAiConfig): RinneAiChat = RinneAiChatImpl(defaultConfig)
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
    val apiKey: String,
    val model: RinneAiModel = RinneAiModel.default,
)


data class RinneAiMessageAnswer(
    val content: String,
)
