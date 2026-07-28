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

sealed interface RinneAiProvider {
    data object OpenAi : RinneAiProvider
    data object Gemini : RinneAiProvider
}

sealed interface RinneAiModel {
    sealed interface OpenAiModel : RinneAiModel
    sealed interface GeminiModel : RinneAiModel

    data object ChatGpt5Nano : OpenAiModel
    data object ChatGpt5mini : OpenAiModel
    data object ChatGpt41 : OpenAiModel
    data object ChatGpt41Mini : OpenAiModel
    data object ChatGpt41Nano : OpenAiModel
    data object ChatGpt4oMini : OpenAiModel
    data object ChatGpt4o : OpenAiModel

    data object Gemini15Flash : GeminiModel
    data object Gemini15Pro : GeminiModel
    data object Gemini20Flash : GeminiModel
    data object Gemini20FlashLite : GeminiModel
    data object Gemini3ProPreview : GeminiModel
    data object Gemini3FlashPreview : GeminiModel
    data object Gemini3ProImagePreview : GeminiModel

    companion object {
        val default = ChatGpt4oMini
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
    val provider: RinneAiProvider = RinneAiProvider.OpenAi,
    val model: RinneAiModel = RinneAiModel.default,
    val enableWebSearch: Boolean = false,
)


data class RinneAiMessageAnswer(
    val content: String,
)
