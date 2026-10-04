package com.rinne.libraries.network.client.offline.cache

import com.rinne.libraries.date.time.core.inWholeMilliseconds
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.core.isSuccessful
import com.rinne.libraries.network.client.core.model.asTextOrNull
import com.rinne.libraries.network.client.core.model.get
import com.rinne.libraries.network.client.core.model.with
import com.rinne.libraries.network.client.offline.RinneRequestExecutor
import com.rinne.libraries.network.client.offline.request.RinneRequestSpec
import com.rinne.libraries.network.client.offline.store.RinneResponseStore
import com.rinne.libraries.network.client.offline.store.RinneStoredResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Two-tier response storage (in-memory for non-persisted requests, the persistent store otherwise)
 * plus network fetching with de-duplication: concurrent fetches of one key share a single request.
 */
internal class RinneResponseCache(
    private val executor: RinneRequestExecutor,
    private val memory: RinneResponseStore,
    private val persistent: RinneResponseStore?,
    private val clock: () -> Long,
    private val coroutineScope: CoroutineScope,
) {
    private val inFlightMutex = Mutex()
    private val inFlight = mutableMapOf<String, CompletableDeferred<RinneStoredResponse>>()
    private var lastStoredAt = 0L

    fun observe(key: String): Flow<RinneStoredResponse?> = when (persistent) {
        null -> memory.observe(key)
        else -> combine(memory.observe(key), persistent.observe(key), ::newest)
    }

    suspend fun get(key: String): RinneStoredResponse? = newest(memory.get(key), persistent?.get(key))

    /** Responses kept across restarts; in-memory ones (searches, third-party pages) are throwaway. */
    suspend fun getAllPersistent(scope: String): List<RinneStoredResponse> = persistent?.getAll(scope).orEmpty()

    suspend fun remove(key: String) {
        memory.remove(key)
        persistent?.remove(key)
    }

    suspend fun markStale(scope: String, tags: Set<String>) {
        memory.markStale(scope, tags)
        persistent?.markStale(scope, tags)
    }

    suspend fun clear(scope: String) {
        memory.clear(scope)
        persistent?.clear(scope)
    }

    /** Fetches [spec] from the network and stores it; joins a fetch of the same key already running. */
    suspend fun fetch(key: String, scope: String, spec: RinneRequestSpec, options: RinneCacheOptions): RinneStoredResponse {
        val deferred = inFlightMutex.withLock {
            inFlight[key] ?: CompletableDeferred<RinneStoredResponse>().also { result ->
                inFlight[key] = result
                // Runs in the client scope so one subscriber leaving doesn't cancel the shared fetch;
                // failures go to the waiters only, never to the scope.
                coroutineScope.launch {
                    try {
                        result.complete(load(key, scope, spec, options))
                    } catch (e: CancellationException) {
                        result.cancel(e)
                        throw e
                    } catch (e: Throwable) {
                        result.completeExceptionally(e)
                    } finally {
                        withContext(NonCancellable) { inFlightMutex.withLock { inFlight.remove(key) } }
                    }
                }
            }
        }
        return deferred.await()
    }

    private suspend fun load(key: String, scope: String, spec: RinneRequestSpec, options: RinneCacheOptions): RinneStoredResponse {
        val existing = get(key)
        val etag = existing?.etag
        val request = if (etag != null) spec.copy(headers = spec.headers.with(IF_NONE_MATCH, etag)) else spec
        val response = executor.execute(request)
        val now = nextStoredAt()
        val expiresAt = options.maxAge?.let { now + it.inWholeMilliseconds }

        val entry = when {
            response.status?.value == NOT_MODIFIED && existing != null -> existing.copy(
                storedAtMillis = now,
                expiresAtMillis = expiresAt,
                tags = existing.tags + options.tags,
            )

            response.isSuccessful -> RinneStoredResponse(
                key = key,
                scope = scope,
                path = spec.path,
                parameters = spec.parameters,
                body = response.body.asTextOrNull().orEmpty(),
                etag = response.headers[ETAG],
                storedAtMillis = now,
                expiresAtMillis = expiresAt,
                tags = options.tags,
            )

            else -> throw RinneNetworkException.Http(response)
        }

        val target = if (options.persist) persistent ?: memory else memory
        target.put(entry)
        return entry
    }

    /** A point in write time: everything stored before it is older, everything stored after it newer. */
    suspend fun writeMarker(): Long = nextStoredAt()

    /**
     * Strictly increasing write time, so of two entries for one key the later write always wins,
     * even within the same millisecond.
     */
    private suspend fun nextStoredAt(): Long = inFlightMutex.withLock {
        maxOf(clock(), lastStoredAt + 1).also { lastStoredAt = it }
    }

    private companion object {
        const val IF_NONE_MATCH = "If-None-Match"
        const val ETAG = "ETag"
        const val NOT_MODIFIED = 304
    }
}

internal fun newest(first: RinneStoredResponse?, second: RinneStoredResponse?): RinneStoredResponse? = when {
    first == null -> second
    second == null -> first
    second.storedAtMillis >= first.storedAtMillis -> second
    else -> first
}
