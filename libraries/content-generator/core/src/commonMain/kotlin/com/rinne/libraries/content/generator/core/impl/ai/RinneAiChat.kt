package com.rinne.libraries.content.generator.core.impl.ai

import com.rinne.libraries.content.generator.core.ai.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal class RinneAiChatImpl : RinneAiChat {
    private val networkProvider by lazy<RinneAiChatNetworkProvider> { RinneAiChatNetworkProviderImpl() }

    override var defaultConfig: RinneAiConfig = RinneAiConfig(RinneAiModel.default)
        private set

    override fun updateDefaultConfig(config: RinneAiConfig) {
        defaultConfig = config
    }

    override suspend fun sendMessage(message: RinneAiChatMessage): RinneAiMessageAnswer {
        return networkProvider.httpClient.post("https://api.openai.com/v1/chat/completions") {
            contentType(ContentType.Application.Json)
            headers {
//                TODO
//                bearerAuth(API_KEY)
            }
            setBody(message.asNetworkRequest(defaultConfig))
        }.body<NetworkAiResponse>().asRinneAiMessageAnswer()
    }
}

internal fun RinneAiModel.key() = when (this) {
    RinneAiModel.ChatGpt4oMini -> "gpt-4o-mini"
    RinneAiModel.ChatGpt4o -> "gpt-4o"
    RinneAiModel.ChatGpt5mini -> "gpt-5-mini"
}


internal fun NetworkAiResponse.asRinneAiMessageAnswer(): RinneAiMessageAnswer {
    return RinneAiMessageAnswer(choices.firstOrNull()?.message?.content ?: "error")
}

internal fun RinneAiChatMessage.asNetworkRequest(defaultConfig: RinneAiConfig): NetworkAiRequest {
    val config = (customConfig ?: defaultConfig).model.key()

    return NetworkAiRequest(
        model = config,
        store = true,
        instructions = current.instructions,
        messages = history.map {
            NetworkAiMessageRequest(role = "user", content = it.message)
        } + listOf(
            NetworkAiMessageRequest(role = "user", content = current.message),
        )
    )
}


@Serializable
data class NetworkAiRequest(
    @SerialName("model") val model: String,
    @SerialName("store") val store: Boolean,
    @SerialName("instructions") val instructions: String?,
    @SerialName("messages") val messages: List<NetworkAiMessageRequest>,
)

@Serializable
data class NetworkAiMessageRequest(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)

@Serializable
data class NetworkAiResponse(
    @SerialName("id") val id: String,
    @SerialName("model") val model: String,
    @SerialName("created") val created: Long,
    @SerialName("choices") val choices: List<NetworkAiChoiceResponse>,
)

@Serializable
data class NetworkAiChoiceResponse(
    @SerialName("index") val index: Int,
    @SerialName("message") val message: NetworkAiMessageResponse,
)

@Serializable
data class NetworkAiMessageResponse(
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
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
