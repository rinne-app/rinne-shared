package com.rinne.libraries.network.client.offline.store

import kotlinx.coroutines.flow.Flow

enum class RinneMutationStatus {
    /** Waiting in the outbox. */
    Pending,

    /** Accepted by the server; kept briefly so optimistic state holds until caches refetch. */
    Sent,

    /** Rejected permanently; its optimistic changes are no longer applied. */
    Failed,
}

/** A queued write request, persisted with everything needed to replay it after a restart. */
data class RinnePendingMutation(
    val id: String,
    val scope: String,
    /** Global FIFO order within [scope]. */
    val sequence: Long,
    val method: String,
    val path: String,
    val parameters: Map<String, List<String>>,
    val body: String?,
    val contentType: String?,
    /** Caller headers only — auth is added at send time and never persisted. */
    val headers: Map<String, String>,
    /** Sent on every attempt so the server applies the mutation at most once. */
    val idempotencyKey: String,
    /** Temporary id of the entity this mutation creates, if any. */
    val tempId: String?,
    val serverId: String?,
    val idField: String,
    val invalidates: Set<String>,
    val status: RinneMutationStatus,
    val attempts: Int,
    val nextAttemptAtMillis: Long,
    val createdAtMillis: Long,
    val sentAtMillis: Long?,
    val failureCode: Int?,
    val failureMessage: String?,
)

interface RinneMutationStore {
    /** All mutations of [scope], ordered by [RinnePendingMutation.sequence]. */
    fun observe(scope: String): Flow<List<RinnePendingMutation>>

    suspend fun getAll(scope: String): List<RinnePendingMutation>

    suspend fun upsert(mutation: RinnePendingMutation)

    suspend fun delete(ids: Collection<String>)

    suspend fun clear(scope: String)

    fun observeIdMappings(scope: String): Flow<Map<String, String>>

    suspend fun getIdMappings(scope: String): Map<String, String>

    suspend fun putIdMapping(scope: String, tempId: String, serverId: String)
}
