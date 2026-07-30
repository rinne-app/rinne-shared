package com.rinne.libraries.content.generator.core.impl.ai

import com.rinne.libraries.content.generator.core.ai.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

internal class RinneAiChatImpl(
    defaultConfig: RinneAiConfig,
) : RinneAiChat {
    private val networkProvider by lazy<RinneAiChatNetworkProvider> { RinneAiChatNetworkProviderImpl() }

    override var defaultConfig: RinneAiConfig = defaultConfig
        private set

    override fun updateDefaultConfig(config: RinneAiConfig) {
        defaultConfig = config
    }

    override suspend fun sendMessage(message: RinneAiChatMessage): RinneAiMessageAnswer {
        val config = message.customConfig ?: defaultConfig

        return when (config.provider) {
            RinneAiProvider.OpenAi -> sendOpenAiMessage(message, config)
            RinneAiProvider.Gemini -> sendGeminiMessage(message, config)
        }
    }

    private suspend fun sendOpenAiMessage(
        message: RinneAiChatMessage,
        config: RinneAiConfig,
    ): RinneAiMessageAnswer {
        if (config.enableWebSearch) {
            return sendOpenAiResponsesMessage(message, config)
        }

        val response = networkProvider.httpClient.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            headers {
                bearerAuth(config.apiKey)
            }
            setBody(message.asOpenAiRequest(config))
        }

        return response.bodyOrThrow<OpenAiResponse>("OpenAI").asRinneAiMessageAnswer()
    }

    private suspend fun sendOpenAiResponsesMessage(
        message: RinneAiChatMessage,
        config: RinneAiConfig,
    ): RinneAiMessageAnswer {
        val response = networkProvider.httpClient.post("https://api.openai.com/v1/responses") {
            contentType(ContentType.Application.Json)
            headers {
                bearerAuth(config.apiKey)
            }
            setBody(message.asOpenAiResponsesRequest(config))
        }

        return response.bodyOrThrow<OpenAiResponsesResponse>("OpenAI").asRinneAiMessageAnswer()
    }

    private suspend fun sendGeminiMessage(
        message: RinneAiChatMessage,
        config: RinneAiConfig,
    ): RinneAiMessageAnswer {
        val model = config.model.geminiKey()
        val url =
            "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=${config.apiKey}"

        val response = networkProvider.httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(message.asGeminiRequest(config))
        }

        return response.bodyOrThrow<GeminiGenerateContentResponse>("Gemini").asRinneAiMessageAnswer()
    }
}

/**
 * A non-2xx response (e.g. a 429 rate-limit) has a completely different JSON shape from the success
 * schema `T` — decoding it as `T` throws an opaque JsonConvertException instead of surfacing what the
 * provider actually said went wrong. Check the status first and surface the provider's own error message.
 */
private suspend inline fun <reified T> HttpResponse.bodyOrThrow(provider: String): T {
    if (status.isSuccess()) return body()

    val rawBody = runCatching { bodyAsText() }.getOrDefault("")
    val message = runCatching { aiErrorJson.decodeFromString<AiErrorEnvelope>(rawBody).error.message }
        .getOrNull()
        ?: rawBody.ifBlank { null }
        ?: "no response body"

    error("$provider API error ${status.value} ${status.description}: $message")
}

private val aiErrorJson = Json { ignoreUnknownKeys = true }

/** Covers both OpenAI's and Gemini's error response shape: {"error": {"message": "...", ...}}. */
@Serializable
private data class AiErrorEnvelope(
    @SerialName("error") val error: AiErrorDetail,
)

@Serializable
private data class AiErrorDetail(
    @SerialName("message") val message: String? = null,
)

internal fun RinneAiModel.openAiKey() = when (this) {
    RinneAiModel.ChatGpt5Nano -> "gpt-5-nano"
    RinneAiModel.ChatGpt4oMini -> "gpt-4o-mini"
    RinneAiModel.ChatGpt4o -> "gpt-4o"
    RinneAiModel.ChatGpt5mini -> "gpt-5-mini"
    RinneAiModel.ChatGpt41 -> "gpt-4.1"
    RinneAiModel.ChatGpt41Mini -> "gpt-4.1-mini"
    RinneAiModel.ChatGpt41Nano -> "gpt-4.1-nano"
    else -> error("Model is not OpenAI-compatible: $this")
}

internal fun RinneAiModel.geminiKey() = when (this) {
    RinneAiModel.Gemini15Flash -> "gemini-1.5-flash"
    RinneAiModel.Gemini15Pro -> "gemini-1.5-pro"
    RinneAiModel.Gemini20Flash -> "gemini-2.0-flash"
    RinneAiModel.Gemini20FlashLite -> "gemini-2.0-flash-lite"
    RinneAiModel.Gemini3ProPreview -> "gemini-3-pro-preview"
    RinneAiModel.Gemini3FlashPreview -> "gemini-3-flash-preview"
    RinneAiModel.Gemini3ProImagePreview -> "gemini-3-pro-image-preview"
    else -> error("Model is not Gemini-compatible: $this")
}

internal fun OpenAiResponse.asRinneAiMessageAnswer(): RinneAiMessageAnswer {
    return RinneAiMessageAnswer(choices.firstOrNull()?.message?.content ?: "error")
}

internal fun GeminiGenerateContentResponse.asRinneAiMessageAnswer(): RinneAiMessageAnswer {
    return RinneAiMessageAnswer(candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "error")
}

internal fun RinneAiChatMessage.asOpenAiRequest(config: RinneAiConfig): OpenAiRequest {
    val model = config.model.openAiKey()

    return OpenAiRequest(
        model = model,
        store = true,
        instructions = current.instructions,
        messages = history.map {
            OpenAiMessageRequest(role = "user", content = it.message)
        } + listOf(
            OpenAiMessageRequest(role = "user", content = current.message),
        )
    )
}

internal fun RinneAiChatMessage.asOpenAiResponsesRequest(config: RinneAiConfig): OpenAiResponsesRequest {
    val model = config.model.openAiKey()

    return OpenAiResponsesRequest(
        model = model,
        instructions = current.instructions,
        input = history.map {
            OpenAiMessageRequest(role = "user", content = it.message)
        } + listOf(
            OpenAiMessageRequest(role = "user", content = current.message),
        ),
        tools = listOf(OpenAiResponsesTool(type = "web_search")),
    )
}

internal fun RinneAiChatMessage.asGeminiRequest(config: RinneAiConfig): GeminiGenerateContentRequest {
    val systemInstruction = current.instructions
        ?.takeIf { it.isNotBlank() }
        ?.let {
            GeminiContent(parts = listOf(GeminiPart(text = it)))
        }

    return GeminiGenerateContentRequest(
        systemInstruction = systemInstruction,
        contents = history.map {
            GeminiContent(role = "user", parts = listOf(GeminiPart(text = it.message)))
        } + listOf(
            GeminiContent(role = "user", parts = listOf(GeminiPart(text = current.message))),
        ),
        tools = if (config.enableWebSearch) listOf(GeminiTool(googleSearch = GeminiGoogleSearch())) else null,
    )
}

@Serializable
data class OpenAiRequest(
    @SerialName("model") val model: String,
    @SerialName("store") val store: Boolean,
    @SerialName("instructions") val instructions: String?,
    @SerialName("messages") val messages: List<OpenAiMessageRequest>,
)

@Serializable
data class OpenAiMessageRequest(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class OpenAiResponse(
    @SerialName("id") val id: String,
    @SerialName("model") val model: String,
    @SerialName("created") val created: Long,
    @SerialName("choices") val choices: List<OpenAiChoiceResponse>,
)

@Serializable
data class OpenAiChoiceResponse(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: OpenAiMessageResponse,
)

@Serializable
data class OpenAiMessageResponse(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class OpenAiResponsesRequest(
    @SerialName("model") val model: String,
    @SerialName("input") val input: List<OpenAiMessageRequest>,
    @SerialName("instructions") val instructions: String? = null,
    @SerialName("tools") val tools: List<OpenAiResponsesTool>? = null,
)

@Serializable
data class OpenAiResponsesTool(
    @SerialName("type") val type: String,
)

@Serializable
data class OpenAiResponsesResponse(
    @SerialName("output") val output: List<OpenAiResponsesOutputItem> = emptyList(),
)

@Serializable
data class OpenAiResponsesOutputItem(
    @SerialName("type") val type: String,
    @SerialName("content") val content: List<OpenAiResponsesContent>? = null,
)

@Serializable
data class OpenAiResponsesContent(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String? = null,
)

internal fun OpenAiResponsesResponse.asRinneAiMessageAnswer(): RinneAiMessageAnswer {
    val text = output
        .firstOrNull { it.type == "message" }
        ?.content
        ?.filter { it.type == "output_text" }
        ?.joinToString("") { it.text.orEmpty() }
        ?.takeIf { it.isNotBlank() }
        ?: "error"

    return RinneAiMessageAnswer(text)
}

@Serializable
data class GeminiGenerateContentRequest(
    @SerialName("system_instruction") val systemInstruction: GeminiContent?,
    @SerialName("contents") val contents: List<GeminiContent>,
    @SerialName("tools") val tools: List<GeminiTool>? = null,
)

@Serializable
data class GeminiTool(
    @SerialName("google_search") val googleSearch: GeminiGoogleSearch,
)

@Serializable
class GeminiGoogleSearch

@Serializable
data class GeminiContent(
    @SerialName("role") val role: String? = null,
    @SerialName("parts") val parts: List<GeminiPart>,
)

@Serializable
data class GeminiPart(
    @SerialName("text") val text: String? = null,
)

@Serializable
data class GeminiGenerateContentResponse(
    @SerialName("candidates") val candidates: List<GeminiCandidate> = emptyList(),
)

@Serializable
data class GeminiCandidate(
    @SerialName("content") val content: GeminiContent? = null,
)

/*
    "id": "chatcmpl-BTU2zkhyRjOHw3O4581m3uDelZHZm",
    "object": "chat.completion",
    "created": 1746366713,
    "model": "gpt-4o-mini-2024-07-18",
    "choices": [
        {
            "index": 0,
            "message": {
                "role": "assistant",
                "content": "Codes that learn and grow,  \nSilent whispers of the mind,  \nFuture's spark ignites.  ",
                "refusal": null,
                "annotations": []
            },
            "logprobs": null,
            "finish_reason": "stop"
        }
    ],
    "usage": {
        "prompt_tokens": 13,
        "completion_tokens": 22,
        "total_tokens": 35,
        "prompt_tokens_details": {
            "cached_tokens": 0,
            "audio_tokens": 0
        },
        "completion_tokens_details": {
            "reasoning_tokens": 0,
            "audio_tokens": 0,
            "accepted_prediction_tokens": 0,
            "rejected_prediction_tokens": 0
        }
    },
    "service_tier": "default",
    "system_fingerprint": "fp_129a36352a"
}
 */
