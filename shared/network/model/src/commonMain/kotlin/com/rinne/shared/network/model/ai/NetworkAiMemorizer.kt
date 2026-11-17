package com.rinne.shared.network.model.ai

import kotlinx.serialization.Serializable

/**
 * `POST /api/v1/ai/memorizer/fields`: fill [targets] of a card of the set [setName] whose current
 * [values] (field id → text) are given. The answer has one value per target it could fill.
 */
@Serializable
data class NetworkAiMemorizerFieldsRequest(
    val values: Map<String, String>,
    val targets: List<NetworkAiMemorizerFieldTarget>,
    val frontLanguage: String? = null,
    val backLanguage: String? = null,
    val setName: String = "",
)

/** A field to fill: its [id], [name], [hint] (placeholder) and [prompt] (the field's AI instruction; empty = from the name). */
@Serializable
data class NetworkAiMemorizerFieldTarget(
    val id: String,
    val name: String,
    val hint: String = "",
    val prompt: String = "",
)

/** The generated text per target field id. */
@Serializable
data class NetworkAiMemorizerFieldsResponse(
    val values: Map<String, String>,
)

/** Error codes of the AI endpoints. */
object AiErrorCodes {
    /** The server has no AI provider configured (503). */
    const val Unavailable = "ai_unavailable"

    /** Too many AI requests by this user (429 with `Retry-After`). */
    const val RateLimited = "ai_rate_limited"

    /** The provider failed or answered something unusable (502). */
    const val Failed = "ai_failed"
}

/** Limits of the AI endpoints. */
object NetworkAi {
    /** The most target fields of one request. */
    const val MaxTargets = 16

    /** The longest value or prompt of a request, in characters. */
    const val MaxTextLength = 4_000
}
