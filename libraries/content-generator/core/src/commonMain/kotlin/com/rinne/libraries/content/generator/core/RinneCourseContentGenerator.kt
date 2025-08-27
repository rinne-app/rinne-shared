package com.rinne.libraries.content.generator.core

import com.rinne.libraries.content.generator.core.ai.RinneAiChat
import com.rinne.libraries.content.generator.core.ai.RinneAiChatMessage
import com.rinne.libraries.content.generator.core.ai.RinneAiMessage
import com.rinne.libraries.content.generator.core.impl.ai.RinneAiChatNetworkProvider
import com.rinne.libraries.content.generator.core.impl.ai.RinneAiChatNetworkProviderImpl
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


interface RinneCourseContentGenerator {
    suspend fun generateThemeExplanations(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String = "English",
    ): String

    suspend fun generateThemeExercises(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String = "English",
        amount: Int = 25,
    ): List<RinneAiCourseExercise>

    companion object {
        fun Default(apiKey: String) = RinneAiCourseContentGenerator(RinneAiChat.Default(apiKey))
    }
}

class RinneAiCourseContentGenerator(
    private val aiChat: RinneAiChat,
) : RinneCourseContentGenerator {
    //TODO
    private val json: Json by lazy { RinneAiChatNetworkProviderImpl().json }

    override suspend fun generateThemeExercises(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String,
        amount: Int
    ): List<RinneAiCourseExercise> {
        val message = RinneAiChatMessage(
            current = RinneAiMessage(
                instructions = null,
                message = """
                    Generate at least $amount exercises on $topic for $language (level $level).  
                    Output: valid JSON array only. No markdown, no ```json, no explanations.  
                    Each item:
                    {
                      "front": sentence in $outputLanguage,
                      "back": correct sentence in $language
                    }
    """.trimIndent()
            ),
        )

        val answer = aiChat.sendMessage(message).content

        return json.parseToJsonElement(answer).jsonArray
            .mapNotNull {
                //TODO
                val front = it.jsonObject["front"]?.jsonPrimitive?.content ?: return@mapNotNull null
                val back = it.jsonObject["back"]?.jsonPrimitive?.content ?: return@mapNotNull null

                RinneAiCourseExercise(
                    front = front,
                    back = back
                )
            }
    }

    override suspend fun generateThemeExplanations(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String,
    ): String {
        val message = RinneAiChatMessage(
            current = RinneAiMessage(
                instructions = null,
                message = """You're a professional linguist and language teacher. Write a comprehensive, self-study-friendly grammar lesson.

Language: $language
Level: $level
Grammar topic: $topic
Output language: $outputLanguage

Requirements:
- Explain the topic in as much depth and detail as possible.
- Cover rules, usage, word order, common mistakes, comparisons, and exceptions.
- If there’s anything else relevant or useful, include it too — the goal is to make the explanation as complete as possible.
- Provide clear examples with translations, and tables or diagrams if helpful.
- Use Markdown: title, overview, main explanation, examples, comparisons, and summary.
- Style: clear, neutral, accessible.
- Don’t include exercises or external links.

Now generate the lesson.
       """.trimIndent()
            ),
        )


        return aiChat.sendMessage(message).content
    }
}

data class RinneAiCourseExercise(
    val front: String,
    val back: String,
)