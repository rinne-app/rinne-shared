package com.rinne.libraries.content.generator.core

import com.rinne.libraries.content.generator.core.ai.RinneAiChat
import com.rinne.libraries.content.generator.core.ai.RinneAiChatMessage
import com.rinne.libraries.content.generator.core.ai.RinneAiConfig
import com.rinne.libraries.content.generator.core.ai.RinneAiMessage

interface RinneTranslationGenerator {

    suspend fun generate(
        input: String,
        language1: String,
        language2: String,
    ): String

    companion object {
        fun Default(apiKey: String): RinneTranslationGenerator =
            RinneTranslationGeneratorImpl(RinneAiChat.Default(apiKey))

        fun Default(defaultConfig: RinneAiConfig): RinneTranslationGenerator =
            RinneTranslationGeneratorImpl(RinneAiChat.Default(defaultConfig))
    }
}


internal class RinneTranslationGeneratorImpl(
    private val aiChat: RinneAiChat,
) : RinneTranslationGenerator {

    override suspend fun generate(
        input: String,
        language1: String,
        language2: String,
    ): String {
        val message = RinneAiChatMessage(
            current = RinneAiMessage(
                instructions = null,
                message = """You are a professional translation engine.
You are given:
- text: $input
- language_a: $language1
- language_b: $language2

Your task:
1. Automatically detect the language of the input text.
2. If the text is written in language_a, translate it into language_b.
3. If the text is written in language_b, translate it into language_a.
4. Translate the text accurately and naturally.
5. Preserve the original meaning, tone, and style.
6. Do not add explanations, comments, or alternative translations.
7. Return only the translated text, without quotes or extra formatting.
""".trimIndent()
            ),
        )

        return aiChat.sendMessage(message).content
    }

}