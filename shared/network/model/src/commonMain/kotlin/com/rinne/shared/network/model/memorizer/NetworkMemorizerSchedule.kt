package com.rinne.shared.network.model.memorizer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NetworkMemorizerScheduleItemRole {
    NEW, REVIEW;

    companion object
}

@Serializable
data class NetworkMemorizerSchedulePlanItem(
    @SerialName("id") val id: String,
    @SerialName("cardId") val cardId: String,
    @SerialName("role") val role: NetworkMemorizerScheduleItemRole,
    @SerialName("orderIndex") val orderIndex: Int,
    @SerialName("completedAt") val completedAt: String? = null,
    @SerialName("card") val card: NetworkMemorizerCardInfo,
)

@Serializable
data class NetworkMemorizerSchedulePlan(
    @SerialName("id") val id: String,
    @SerialName("setId") val setId: String,
    @SerialName("planDate") val planDate: String,
    @SerialName("generatedAt") val generatedAt: String,
    @SerialName("regeneratedAt") val regeneratedAt: String? = null,
    @SerialName("items") val items: List<NetworkMemorizerSchedulePlanItem>,
    @SerialName("isCompleted") val isCompleted: Boolean,
)

@Serializable
data class NetworkMemorizerScheduleHistoryEntry(
    @SerialName("planDate") val planDate: String,
    @SerialName("totalItems") val totalItems: Int,
    @SerialName("completedItems") val completedItems: Int,
    @SerialName("completionPercentage") val completionPercentage: Double,
    @SerialName("newCount") val newCount: Int,
    @SerialName("reviewCount") val reviewCount: Int,
    @SerialName("isCompleted") val isCompleted: Boolean,
)

@Serializable
data class NetworkMemorizerScheduleHistory(
    @SerialName("entries") val entries: List<NetworkMemorizerScheduleHistoryEntry>,
)
