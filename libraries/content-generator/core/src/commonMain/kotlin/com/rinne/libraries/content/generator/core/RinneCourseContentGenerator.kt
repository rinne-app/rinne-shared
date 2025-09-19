package com.rinne.libraries.content.generator.core

import com.rinne.libraries.content.generator.core.ai.RinneAiChat
import com.rinne.libraries.content.generator.core.ai.RinneAiChatMessage
import com.rinne.libraries.content.generator.core.ai.RinneAiMessage
import com.rinne.libraries.content.generator.core.impl.ai.RinneAiChatNetworkProviderImpl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
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

    suspend fun generateGrammarThemes(
        language: String,
        outputLanguage: String = "English",
    ): List<RinneGeneratorGrammarTheme>

    suspend fun generateGrammarThemeExplanations(
        language: String,
        topic: String,
        outputLanguage: String = "English",
    ): String

    suspend fun generateThemeExercises(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String = "English",
        amount: Int = 25,
    ): List<RinneAiCourseExercise>

    suspend fun generateThemesForLevel(
        language: String,
        outputLanguage: String,
        level: String,
    ): List<String>

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
                message = RinneCoursePrompts.themeExercises(
                    language = language,
                    topic = topic,
                    level = level,
                    outputLanguage = outputLanguage,
                    amount = amount
                )
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

    override suspend fun generateThemesForLevel(
        language: String,
        outputLanguage: String,
        level: String,
    ): List<String> {
        val answer = aiChat.sendMessage(
            RinneAiChatMessage(
                current = RinneAiMessage(
                    instructions = null,
                    message = RinneCoursePrompts.themesForLevel(
                        level = level,
                        language = language,
                        outputLanguage = outputLanguage
                    )
                )
            )
        ).content

        return json.parseToJsonElement(answer).jsonArray.mapNotNull { it.jsonPrimitive.content }
    }

    override suspend fun generateGrammarThemes(
        language: String,
        outputLanguage: String
    ): List<RinneGeneratorGrammarTheme> {
        val message = RinneAiChatMessage(
            current = RinneAiMessage(
                instructions = null,
                message = RinneCoursePrompts.grammarThemes(
                    language = language,
                    outputLanguage = outputLanguage,
                )
            ),
        )

        val answer = aiChat.sendMessage(message).content
//        val answer =
//            """""".trimIndent()

        return json
            .decodeFromString<List<NetworkGrammarTheme>>(answer)
            .map { it.asRinneGeneratorGrammarTheme() }
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
                message = RinneCoursePrompts.defaultThemeExplanations(
                    topic = topic,
                    level = level,
                    language = language,
                    outputLanguage = outputLanguage
                )
            ),
        )


        return aiChat.sendMessage(message).content
    }

    override suspend fun generateGrammarThemeExplanations(
        language: String,
        topic: String,
        outputLanguage: String
    ): String {
        val message = RinneAiChatMessage(
            current = RinneAiMessage(
                instructions = null,
                message = RinneCoursePrompts.grammarThemeExplanations(
                    topic = topic,
                    language = language,
                    outputLanguage = outputLanguage
                ),
            )
        )

        return aiChat.sendMessage(message).content
    }
}

data class RinneAiCourseExercise(
    val front: String,
    val back: String,
)


internal object RinneCoursePrompts {
    fun themeExercises(
        language: String,
        topic: String,
        level: String,
        outputLanguage: String,
        amount: Int,
    ) = """
    Generate at least $amount exercises on $topic for $language (level $level).  
    Output: valid JSON array only. No markdown, no ```json, no explanations.  
    Each item:
    {
      "front": sentence in $outputLanguage,
      "back": correct sentence in $language
    }
    """.trimIndent()

    fun themesForLevel(
        level: String,
        language: String,
        outputLanguage: String,
    ) = """
You are an expert CEFR-aligned language curriculum designer.

Language: $language
Requested CEFR level: $level
Output language for topic titles: $outputLanguage

Task:
Generate a maximally detailed, exhaustive list of learning topics ("themes") for the specified language and CEFR level.

STRICT OUTPUT RULES:
- Output MUST be a valid JSON array of strings.
- Do NOT output any JSON object, only a pure array: [ "topic1", "topic2", ... ].
- Do NOT include Markdown code fences (```), labels, or any extra text before or after the array.
- Each array element must be a short topic title in $outputLanguage (2–8 words).
- Cover grammar, vocabulary, communicative functions, pronunciation, discourse, skills, collocations, idioms, errors, register, and cultural aspects.
- Be exhaustive for the requested CEFR level (simpler for A1/A2, broader and deeper for higher levels).
- If the level is "all" or multiple levels, output a single flat array with all topics combined, NOT grouped by level.

The only output should be the JSON array.
""".trimIndent()


    fun defaultThemeExplanations(
        topic: String,
        level: String,
        language: String,
        outputLanguage: String,
    ) =
        """You're a professional linguist and language teacher. Write a comprehensive, self-study-friendly grammar lesson.

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

    fun grammarThemeExplanations(
        topic: String,
        language: String,
        outputLanguage: String,
    ) = """Variables (define once at the top):
- TOPIC = $topic
- LANGUAGE = $language
- OUTPUT_LANGUAGE = $outputLanguage

Task:
Generate an encyclopedic, maximally detailed Markdown description of TOPIC in LANGUAGE.
- Use OUTPUT_LANGUAGE for all explanations, commentary, and instructions.
- Use LANGUAGE for all examples, sentences, words, phrases, and specialized terminology. Mixed-language insertions are allowed only for technical terms in parentheses.

Requirements:

1. Depth and completeness
- Cover all aspects, subcategories, and related points.
- Include grammatical, phonetic, lexical, syntactic, semantic, pragmatic, stylistic, and practical details.
- Explain all rules, exceptions, and irregularities.
- Include historical or etymological context if relevant.

2. Structure and formatting
- Output pure Markdown (headings, lists, tables if needed).
- Use headings (#, ##, ###) to organize content hierarchically.
- Include lists for examples, categories, or distinctions.
- Use bold or italic for emphasis where appropriate.
- Include code/IPA blocks for phonetic examples if relevant.

3. Examples and illustrations
- Include illustrative examples in LANGUAGE.
- Show exceptions, irregular forms, and contrasting cases.

4. Terminology and language use
- Explanations and commentary in OUTPUT_LANGUAGE.
- Terms, words, phrases, and examples in LANGUAGE.
- Avoid unnecessary simplifications; do not skip nuances.

5. Length and detail
- Produce long-form, encyclopedic content.
- Avoid generic statements; every sentence should add concrete detail.

Instruction:
Write the content as a single, structured Markdown document.
Do not include any text outside the Markdown.
""".trimIndent()

    fun vocabularyCategories(
        language: String,
        outputLanguage: String,
    ) = """Variables:
- LANGUAGE: $language  
- OUTPUT_LANGUAGE: $outputLanguage

You are a language curriculum designer.

Task:
Generate a structured JSON representing all main vocabulary and expression categories for learning LANGUAGE. The output should be exhaustive, detailed, and suitable for all CEFR levels (A1–C2). Include subcategories where necessary. Use OUTPUT_LANGUAGE for all category and subcategory names.

Requirements:
1. JSON format:
[
  {
    "title": "Category Name in OUTPUT_LANGUAGE",
    "subcategories": [
      {
        "name": "Subcategory Name in OUTPUT_LANGUAGE",
        "level": "CEFR Level (optional, can be null)"
      }
    ]
  }
]
2. Include all practical and communicative areas, like: daily life, work, education, travel, hobbies, culture, media, idioms, proverbs, slang.
3. Make it broad and detailed, so that later we can generate individual topics for each subcategory.
4. Output only pure JSON, no explanations or comments.
""".trimIndent()

    fun vocabularyCategoryTopics(
        category: String,
        language: String,
        outputLanguage: String,
    ) = """
        Variables:
        - LANGUAGE: $language
        - CATEGORY_NAME: $category
        - OUTPUT_LANGUAGE: $outputLanguage

        You are a language curriculum designer.

        Task:
        For the CATEGORY_NAME category in LANGUAGE, generate a structured JSON containing all atomic topics learners should know. Include:
        - Topic name (in OUTPUT_LANGUAGE)
        - CEFR level (A1–C2)
        - Target vocabulary size (approximate number of words to master this topic)

        Requirements:
        1. JSON format:
        [
          {
            "name": "Topic Name in OUTPUT_LANGUAGE",
            "level": "CEFR Level",
            "targetVocabularySize": Number
          }
        ]
        2. Make the topics exhaustive and detailed, including daily expressions, idioms, proverbs, slang, and thematic vocabulary.
        3. Ensure topics are atomic, so each can be used to generate a word list or exercises.
        4. Use OUTPUT_LANGUAGE for all names (exceptions for borrowed words or proper nouns are okay).
        5. Output only JSON, no explanations or extra text.

    """.trimIndent()

    fun vocabularyForTopic(
        topic: String,
        level: String,
        language: String,
        outputLanguage: String,
        size: Int,
        grammarTopics: List<String>,
    ) = """
        Variables:
        - TOPIC_NAME: $topic
        - LANGUAGE: $language
        - LEVEL: $level
        - TARGET_VOCAB_SIZE: $size  
        - GRAMMAR_TOPICS: [${grammarTopics.joinToString()}]  # optional, can be empty []
        - TRANSLATION_LANGUAGE: $outputLanguage

        Task:
        Using the variables above, generate a structured JSON vocabulary list for the topic TOPIC_NAME in LANGUAGE. Each entry must include:
        - the word or expression in LANGUAGE,
        - its translation into TRANSLATION_LANGUAGE,
        - a contextual example sentence or situation in LANGUAGE,
        - optional notes about usage, style, register, idiomatic usage, or grammar focus.

        Requirements:
        1. JSON format:
        {
          "name": "TOPIC_NAME",
          "level": "LEVEL",
          "targetVocabularySize": TARGET_VOCAB_SIZE,
          "grammarFocus": GRAMMAR_TOPICS,
          "vocabulary": [
            {
              "word": "word or expression",
              "translation": "translation into TRANSLATION_LANGUAGE",
              "context": "example sentence or typical situation in LANGUAGE",
              "notes": "optional notes about style, register, conversation level, idiomatic usage, or grammar focus"
            }
          ]
        }

        2. Include approximately TARGET_VOCAB_SIZE words/expressions in the list.
        3. Ensure all words/expressions are practical, thematic, and appropriate for LEVEL.
        4. Use the grammatical topics from GRAMMAR_TOPICS when constructing contextual example sentences. If GRAMMAR_TOPICS is empty, select appropriate grammatical constructions at your discretion.
        5. Include idioms, set phrases, or colloquial expressions when relevant.
        6. Output only pure JSON. Do not include explanations, notes, or extra text outside the JSON.
    """.trimIndent()

    fun grammarExercises(
        topic: String,
        language: String,
        outputLanguage: String,
        level: String,
        size: Int,
        lexicalTopics: List<String>,
    ) = """
Variables:
- TOPIC_NAME: $topic
- LANGUAGE: $language
- LEVEL: $level
- TARGET_EXERCISES: $size
- TRANSLATION_LANGUAGE: $outputLanguage
- LEXICAL_TOPICS: $lexicalTopics  # optional, can be empty []

You are a language curriculum designer.

Task:
Generate a structured JSON containing TARGET_EXERCISES exercises for practicing the grammar of TOPIC_NAME in LANGUAGE. Each exercise should focus on applying the grammatical rules of the topic. Optionally, if LEXICAL_TOPICS is provided, incorporate vocabulary from these topics into the example sentences.

Each exercise must include:
- instruction: Instruction in LANGUAGE.
- example: Sentence in LANGUAGE with exactly one blank (___)
    """.trimIndent()

    fun grammarThemes(
        language: String,
        outputLanguage: String,
    ) = """
        Variables:
        - LANG_TARGET = $language"
        - LANG_UI = $outputLanguage"
        
Generate a comprehensive and maximally detailed list of topics for an overview of the {LANG_TARGET} language. Output language: {LANG_UI}. Output format: strict JSON, an array of objects of the form: [ { "title": "…", "topics": ["…", "…", "..."] } ] No text outside JSON. Rules: Sections and topic level Each object in the array = a major section (e.g. Phonetics/Phonology; Orthography/Writing; Morphology; Syntax; Semantics/Lexicology; Pragmatics; Discourse/Registers; Dialects/Varieties; Sociolinguistics; Practice/Communication; History/Etymology/Borrowings). topics must be a flat list of atomic subtopics. No nested objects inside topics. Depth to the finest detail Expand the hierarchy down to the smallest units (e.g. not just “Verbs → Tenses”, but separate topics for “Verbs → Tenses → Present”, “Verbs → Tenses → Past Perfect”, etc.). To preserve hierarchy without nesting, use → (space-arrow-space) as a level separator inside topic strings. Adaptation to the specific language Include only phenomena characteristic of {LANG_TARGET} (unique categories, reduced/absent categories, special features). If a major category is not applicable, add a topic noting this (e.g. “Morphology → Cases → Absent; expressed via prepositions/word order”). Terminology and output language All content must be in {LANG_UI}. For highly specific terms, you may add short parenthetical insertions in another language (e.g. “Pragmatics → Evidentiality (evidentiality)”). Quality and completeness No example sentences or explanations — only topic names. No duplicate or overlapping topics; keep them atomic. Logical order inside each section (general → specific). No empty sections (every section must contain topics). Formatting and validity Strict JSON array, no comments, no Markdown. title must be a non-empty string, topics must be an array of unique non-empty strings. Use correct Unicode characters and diacritics. Generate only JSON according to these rules.
""".trimIndent()
}


@Serializable
data class NetworkGrammarTheme(
    @SerialName("title") val title: String,
    @SerialName("topics") val topics: List<String>,
)

fun NetworkGrammarTheme.asRinneGeneratorGrammarTheme() = RinneGeneratorGrammarTheme(
    title = title,
    topics = topics
)

data class RinneGeneratorGrammarTheme(
    val title: String,
    val topics: List<String>,
)