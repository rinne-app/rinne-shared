package com.rinne.libraries.network.client.offline

import com.rinne.libraries.date.time.core.RinneDateTime
import com.rinne.libraries.date.time.core.RinneDuration
import com.rinne.libraries.date.time.core.hours
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.network.client.offline.cache.RinneCachePolicy
import com.rinne.libraries.network.client.offline.connectivity.RinneConnectivity
import com.rinne.libraries.network.client.offline.interceptor.RinneInterceptor
import com.rinne.libraries.network.client.offline.mutation.RinneConflictResolver
import com.rinne.libraries.network.client.offline.mutation.RinneRetryPolicy
import com.rinne.libraries.network.client.offline.optimistic.RinneOptimisticReducer
import com.rinne.libraries.network.client.offline.optimistic.RinneOptimisticReducersBuilder
import com.rinne.libraries.network.client.offline.store.RinneMutationStore
import com.rinne.libraries.network.client.offline.store.RinneResponseStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class RinneClientConfig internal constructor() {
    var codec: RinneBodyCodec = RinneJsonBodyCodec()

    /** Persistent GET storage; without it responses are only shared in memory for the process lifetime. */
    var responseStore: RinneResponseStore? = null

    /** Persistent outbox; without it `queueIfOffline` mutations are sent directly. */
    var mutationStore: RinneMutationStore? = null

    var connectivity: RinneConnectivity = RinneConnectivity.AlwaysOnline

    /**
     * Partition of the local data (e.g. user id + server). Cache keys and the outbox are per scope,
     * so switching account or server never shows another scope's data.
     */
    var scope: () -> String = { DEFAULT_SCOPE }

    var defaultCachePolicy: RinneCachePolicy = RinneCachePolicy.CacheAndNetwork
    var retryPolicy: RinneRetryPolicy = RinneRetryPolicy()
    var conflictResolver: RinneConflictResolver = RinneConflictResolver.Discard

    /** How long an accepted mutation keeps being applied while the affected GETs refetch. */
    var sentMutationRetention: RinneDuration = 1.hours

    var clock: () -> Long = { RinneDateTime.now().epochMillis }

    /**
     * Response field holding an entity's revision. When a write sent with `If-Match` succeeds, queued
     * writes of the same resource are moved to the revision found here.
     */
    var revisionField: String = DEFAULT_REVISION_FIELD

    /** Receives outbox events (queued, sent, retried, rejected); `null` disables them. */
    var logger: RinneLogger? = null

    /** Runs shared fetches and the outbox; must outlive individual screens. */
    var coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    internal val interceptors = mutableListOf<RinneInterceptor>()
    internal val reducers = mutableListOf<RinneOptimisticReducer<*, *>>()

    /** Interceptors run in the order they are added, the first one sees the request first. */
    fun interceptor(interceptor: RinneInterceptor) {
        interceptors += interceptor
    }

    fun optimistic(block: RinneOptimisticReducersBuilder.() -> Unit) {
        reducers += RinneOptimisticReducersBuilder().apply(block).reducers
    }

    private companion object {
        const val DEFAULT_SCOPE = "default"
        const val DEFAULT_REVISION_FIELD = "revision"
    }
}
