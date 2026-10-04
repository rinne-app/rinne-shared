package com.rinne.libraries.network.client.offline.store

import kotlinx.coroutines.flow.Flow

/** A successful GET response kept as raw text, so the store needs no knowledge of the payload types. */
data class RinneStoredResponse(
    val key: String,
    val scope: String,
    val path: String,
    /** Query of the request, so the stored response can be refetched as it was requested. */
    val parameters: Map<String, List<String>> = emptyMap(),
    val body: String,
    val etag: String?,
    val storedAtMillis: Long,
    /** `null` = never expires; `0` = explicitly invalidated. */
    val expiresAtMillis: Long?,
    val tags: Set<String>,
) {
    fun isFresh(nowMillis: Long) = expiresAtMillis == null || nowMillis < expiresAtMillis
}

/**
 * Local source of truth for GET responses. Every observer of a key is notified on [put], which is
 * how one request's result reaches everyone listening to the same endpoint.
 */
interface RinneResponseStore {
    fun observe(key: String): Flow<RinneStoredResponse?>

    suspend fun get(key: String): RinneStoredResponse?

    suspend fun getAll(scope: String): List<RinneStoredResponse>

    suspend fun put(response: RinneStoredResponse)

    suspend fun remove(key: String)

    /** Expires every response of [scope] tagged with any of [tags]. */
    suspend fun markStale(scope: String, tags: Set<String>)

    suspend fun clear(scope: String)
}
