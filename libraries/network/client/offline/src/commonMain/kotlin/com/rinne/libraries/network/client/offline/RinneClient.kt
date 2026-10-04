package com.rinne.libraries.network.client.offline

import com.rinne.libraries.date.time.core.inWholeMilliseconds
import com.rinne.libraries.date.time.core.milliseconds
import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.core.isSuccessful
import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import com.rinne.libraries.network.client.core.model.asTextOrNull
import com.rinne.libraries.network.client.core.model.with
import com.rinne.libraries.network.client.offline.cache.RinneCacheOptions
import com.rinne.libraries.network.client.offline.cache.RinneCachePolicy
import com.rinne.libraries.network.client.offline.cache.RinneCacheRefreshReport
import com.rinne.libraries.network.client.offline.cache.RinneResponseCache
import com.rinne.libraries.network.client.offline.cache.newest
import com.rinne.libraries.network.client.offline.mutation.IDEMPOTENCY_KEY_HEADER
import com.rinne.libraries.network.client.offline.mutation.RinneMutationResult
import com.rinne.libraries.network.client.offline.mutation.RinneOfflineOptions
import com.rinne.libraries.network.client.offline.mutation.RinneOutbox
import com.rinne.libraries.network.client.offline.mutation.RinneOutboxOutcome
import com.rinne.libraries.network.client.offline.mutation.containsTempId
import com.rinne.libraries.network.client.offline.mutation.extractEntityId
import com.rinne.libraries.network.client.offline.mutation.randomId
import com.rinne.libraries.network.client.offline.optimistic.RinneOptimisticOverlay
import com.rinne.libraries.network.client.offline.request.RinneGetRequestBuilder
import com.rinne.libraries.network.client.offline.request.RinneMutationRequestBuilder
import com.rinne.libraries.network.client.offline.request.RinneRequestSpec
import com.rinne.libraries.network.client.offline.store.InMemoryResponseStore
import com.rinne.libraries.network.client.offline.store.RinneMutationStatus
import com.rinne.libraries.network.client.offline.store.RinnePendingMutation
import com.rinne.libraries.network.client.offline.store.RinneStoredResponse
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * HTTP client with a local source of truth. GETs return a [Flow] that emits the stored value, then the
 * network value, and keeps emitting whenever anyone refreshes the same request; writes can be queued
 * while offline and are applied to GET values optimistically until the server accepts them.
 *
 * ```
 * val groups: Flow<RinneNetworkResult<List<Group>>> = client.get("tasks") {
 *     cache { policy = RinneCachePolicy.CacheAndNetwork; tags("tasks") }
 * }
 * val group: Group = client.get<Group>("tasks/group/$id").await()
 *
 * client.post<Task>("tasks/group/$groupId/task") {
 *     setBody(edit)
 *     offline { queueIfOffline = true; createsEntity(); invalidates("tasks") }
 * }
 * ```
 */
class RinneClient(transport: RinneHttpClient, configure: RinneClientConfig.() -> Unit = {}) {
    private val config = RinneClientConfig().apply(configure)
    private val codec = config.codec
    private val executor = RinneRequestExecutor(transport, config.interceptors.toList(), config.connectivity)
    private val cache = RinneResponseCache(
        executor = executor,
        memory = InMemoryResponseStore(),
        persistent = config.responseStore,
        clock = config.clock,
        coroutineScope = config.coroutineScope,
    )
    private val overlay = RinneOptimisticOverlay(config.reducers.toList(), codec)
    private val invalidations = MutableSharedFlow<Invalidation>(extraBufferCapacity = INVALIDATIONS_BUFFER)
    private val mutationStore = config.mutationStore
    private val outbox = mutationStore?.let { store ->
        RinneOutbox(
            store = store,
            executor = executor,
            connectivity = config.connectivity,
            retryPolicy = config.retryPolicy,
            conflictResolver = config.conflictResolver,
            sentRetentionMillis = config.sentMutationRetention.inWholeMilliseconds,
            clock = config.clock,
            scopeProvider = config.scope,
            onSent = { invalidate(it.scope, it.invalidates) },
            coroutineScope = config.coroutineScope,
            revisionField = config.revisionField,
            logger = config.logger,
        )
    }
    private val outboxBlocked = MutableStateFlow(false)

    init {
        executor.onServerReachable = {
            // A response means the server is reachable: stop waiting out the outbox backoff.
            if (outboxBlocked.value) {
                outboxBlocked.value = false
                config.coroutineScope.launch { outbox?.retryNow() }
            }
        }
    }

    private data class Invalidation(val scope: String, val tags: Set<String>)

    private sealed interface FetchState {
        /** No network request for this subscription: the policy serves the store only. */
        data object Skipped : FetchState
        data object Loading : FetchState
        data class Done(val entry: RinneStoredResponse) : FetchState
        data class Failed(val cause: Throwable) : FetchState
    }

    // region GET

    fun <T> get(
        type: KType,
        path: String,
        block: RinneGetRequestBuilder.() -> Unit = {},
    ): Flow<RinneNetworkResult<T>> {
        val builder = RinneGetRequestBuilder(codec, config.defaultCachePolicy).apply(block)
        val spec = builder.buildSpec(RinneHttpMethod.Get, path)
        val options = builder.buildCacheOptions()
        val scope = config.scope()

        if (!options.observe) {
            return flow {
                val resolved = spec.withResolvedTempIds(idMappings(scope).first())
                emitAll(observe(type, scope, resolved, options))
            }
        }

        // Re-subscribes under the server id as soon as a temporary id in the path gets synced.
        @OptIn(ExperimentalCoroutinesApi::class)
        return idMappings(scope)
            .map { spec.withResolvedTempIds(it) }
            .distinctUntilChanged()
            .flatMapLatest { resolved -> observe(type, scope, resolved, options) }
    }

    private fun <T> observe(
        type: KType,
        scope: String,
        spec: RinneRequestSpec,
        options: RinneCacheOptions,
    ): Flow<RinneNetworkResult<T>> = channelFlow {
        val key = spec.cacheKey(scope)
        // A path with an unsynced temporary id exists only locally (through optimistic reducers).
        val localOnly = spec.path.containsTempId()
        val fetchState = MutableStateFlow<FetchState>(FetchState.Skipped)

        val initial = cache.get(key)
        val needsFetch = !localOnly && when (options.policy) {
            RinneCachePolicy.CacheOnly -> false
            RinneCachePolicy.CacheFirst -> initial == null || !initial.isFresh(config.clock())
            else -> true
        }
        if (needsFetch) fetchState.value = FetchState.Loading
        // Started after the first combination, so the stored value is emitted before the network
        // one even when the network answers instantly.
        val fetchJob = when (needsFetch) {
            true -> launch(start = CoroutineStart.LAZY) { fetchState.value = fetchInto(key, scope, spec, options) }
            false -> null
        }
        if (!localOnly && options.policy != RinneCachePolicy.CacheOnly) {
            launchInvalidationRefresh(key, scope, spec, options, fetchState)
        }

        combine(
            cache.observe(key),
            mutations(scope, type, spec.path),
            idMappings(scope),
            fetchState,
        ) { entry, mutations, mappings, fetch ->
            resolve<T>(type, spec, options, localOnly, entry, mutations, mappings, fetch)
        }
            .onEach { fetchJob?.start() }
            .filterNotNull()
            .distinctUntilChanged()
            .transformWhile {
                emit(it)
                options.observe || !it.isFinal
            }
            .collect { send(it) }

        // Not observing: stop the fetch/invalidation children so the flow completes normally.
        coroutineContext.job.cancelChildren()
    }

    private fun ProducerScope<*>.launchInvalidationRefresh(
        key: String,
        scope: String,
        spec: RinneRequestSpec,
        options: RinneCacheOptions,
        fetchState: MutableStateFlow<FetchState>,
    ) = launch {
        invalidations
            .filter { it.scope == scope && it.tags.any { tag -> tag in options.tags } }
            .collect {
                // Keep showing the current value while refreshing; only replace it on success.
                val result = fetchInto(key, scope, spec, options)
                if (result is FetchState.Done) fetchState.value = result
            }
    }

    private suspend fun fetchInto(
        key: String,
        scope: String,
        spec: RinneRequestSpec,
        options: RinneCacheOptions,
    ): FetchState = try {
        FetchState.Done(cache.fetch(key, scope, spec, options))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        FetchState.Failed(e)
    }

    private fun <T> resolve(
        type: KType,
        spec: RinneRequestSpec,
        options: RinneCacheOptions,
        localOnly: Boolean,
        stored: RinneStoredResponse?,
        mutations: List<RinnePendingMutation>,
        mappings: Map<String, String>,
        fetch: FetchState,
    ): RinneNetworkResult<T>? {
        val path = spec.path
        val fetched = (fetch as? FetchState.Done)?.entry
        val base = newest(stored, fetched)
        val decoded = base?.let { entry ->
            runCatching { codec.decode(entry.body, type) }.getOrElse { error ->
                // A network response that can't be decoded is an error; a stale stored one (e.g.
                // written by an older app version) is just ignored.
                if (entry === fetched) return RinneNetworkResult.Error(error)
                null
            }
        }
        @Suppress("UNCHECKED_CAST")
        val result = overlay.apply(decoded as T?, type, spec, base?.storedAtMillis, mutations, mappings)
        val value = result.value
        val pending = result.hasPendingMutations

        return when (fetch) {
            FetchState.Loading -> when {
                options.policy == RinneCachePolicy.CacheAndNetwork && value != null ->
                    RinneNetworkResult.Data(value, RinneDataSource.Cache, isFinal = false, hasPendingMutations = pending)

                else -> null
            }

            FetchState.Skipped -> when (value) {
                null -> RinneNetworkResult.Error(
                    if (localOnly) RinneEntityNotSyncedException(path) else RinneCacheMissException(path)
                )

                else -> RinneNetworkResult.Data(value, RinneDataSource.Cache, isFinal = true, hasPendingMutations = pending)
            }

            is FetchState.Done -> when (value) {
                null -> RinneNetworkResult.Error(IllegalStateException("Empty value for $path"))
                else -> RinneNetworkResult.Data(value, RinneDataSource.Network, isFinal = true, hasPendingMutations = pending)
            }

            is FetchState.Failed -> when {
                options.policy == RinneCachePolicy.NetworkOnly || value == null -> RinneNetworkResult.Error(fetch.cause)
                else -> RinneNetworkResult.Data(
                    value = value,
                    source = RinneDataSource.Cache,
                    isFinal = true,
                    hasPendingMutations = pending,
                    refreshError = fetch.cause,
                )
            }
        }
    }

    private fun idMappings(scope: String): Flow<Map<String, String>> =
        mutationStore?.observeIdMappings(scope) ?: flowOf(emptyMap())

    /** Only subscribes to the outbox when a reducer could change this request's value. */
    private fun mutations(scope: String, type: KType, path: String): Flow<List<RinnePendingMutation>> =
        when (mutationStore != null && overlay.hasReducersFor(type, path)) {
            true -> mutationStore.observe(scope)
            false -> flowOf(emptyList())
        }

    // endregion

    // region Mutations

    suspend fun <R> mutate(
        method: RinneHttpMethod,
        type: KType,
        path: String,
        block: RinneMutationRequestBuilder.() -> Unit = {},
    ): RinneMutationResult<R> {
        val builder = RinneMutationRequestBuilder(codec).apply(block)
        val spec = builder.buildSpec(method, path)
        val options = builder.buildOfflineOptions()

        return when (outbox != null && options.queueIfOffline) {
            true -> enqueue(outbox, spec, options, type)
            false -> send(spec, options, type)
        }
    }

    private suspend fun <R> send(spec: RinneRequestSpec, options: RinneOfflineOptions, type: KType): RinneMutationResult<R> {
        val scope = config.scope()
        val resolved = spec.withResolvedTempIds(mutationStore?.getIdMappings(scope).orEmpty())
        // Also here: a retried call (e.g. after a timeout) must not create a second entity.
        val response = executor.execute(resolved.copy(headers = resolved.headers.with(IDEMPOTENCY_KEY_HEADER, randomId())))
        if (!response.isSuccessful) throw RinneNetworkException.Http(response)

        invalidate(scope, options.invalidates)
        return RinneMutationResult.Sent(
            value = decodeResponse(response, type),
            entityId = if (options.createsEntity) extractEntityId(response.body.asTextOrNull(), options.idField) else null,
        )
    }

    private suspend fun <R> enqueue(
        outbox: RinneOutbox,
        spec: RinneRequestSpec,
        options: RinneOfflineOptions,
        type: KType,
    ): RinneMutationResult<R> = when (val outcome = outbox.enqueueAndAwait(spec, options)) {
        is RinneOutboxOutcome.Sent -> RinneMutationResult.Sent(
            value = decodeResponse(outcome.response, type),
            entityId = outcome.mutation.serverId,
        )

        is RinneOutboxOutcome.Failed -> throw outcome.cause
        is RinneOutboxOutcome.Deferred -> {
            outboxBlocked.value = true
            RinneMutationResult.Queued(outcome.mutation.id, outcome.mutation.tempId, outcome.cause)
        }

        RinneOutboxOutcome.ResolvedLocally -> RinneMutationResult.ResolvedLocally
    }

    private fun <R> decodeResponse(response: RinneHttpResponse, type: KType): R {
        val text = response.body.asTextOrNull().orEmpty()
        @Suppress("UNCHECKED_CAST")
        return when {
            type.classifier == Unit::class -> Unit as R
            text.isEmpty() && type.isMarkedNullable -> null as R
            else -> codec.decode(text, type) as R
        }
    }

    // endregion

    // region Local data management

    /** Outbox progress of the current scope, e.g. for a "not synced" indicator. */
    fun observeSyncState(): Flow<RinneSyncState> {
        val outbox = outbox ?: return flowOf(RinneSyncState.Idle)
        return combine(observeMutations(), outbox.isSyncing, outbox.lastError) { mutations, syncing, error ->
            RinneSyncState(
                pendingCount = mutations.count { it.status == RinneMutationStatus.Pending },
                failedCount = mutations.count { it.status == RinneMutationStatus.Failed },
                isSyncing = syncing,
                lastError = error,
            )
        }
    }

    /** Unsent and rejected mutations of the current scope, e.g. to warn before logging out. */
    fun observeMutations(): Flow<List<RinnePendingMutation>> =
        mutationStore?.observe(config.scope())?.map { all -> all.filter { it.status != RinneMutationStatus.Sent } }
            ?: flowOf(emptyList())

    suspend fun retryMutation(id: String) = outbox?.retry(id)

    suspend fun discardMutation(id: String) = outbox?.discard(id)

    /** Tries to send queued mutations right away, skipping the backoff wait. */
    suspend fun sync() = outbox?.retryNow()

    /** Marks responses tagged with [tags] stale and refetches the ones currently observed. */
    suspend fun invalidate(vararg tags: String) = invalidate(config.scope(), tags.toSet())

    private suspend fun invalidate(scope: String, tags: Set<String>) {
        if (tags.isEmpty()) return
        cache.markStale(scope, tags)
        invalidations.emit(Invalidation(scope, tags))
    }

    /**
     * Brings the whole local copy up to date: runs [prefetch] (reads to keep even if no screen has
     * opened them yet), then refetches every stored response of the backend that the prefetch didn't
     * just fetch, [parallelism] at a time. Responses the server no longer has are dropped; failures
     * keep the stored response. Queued mutations are sent first so the refetched data includes them.
     */
    suspend fun refreshCache(
        prefetch: List<suspend () -> Unit> = emptyList(),
        parallelism: Int = DEFAULT_REFRESH_PARALLELISM,
    ): RinneCacheRefreshReport {
        if (!config.connectivity.isOnline.value) throw RinneNetworkException.NoConnection()
        val scope = config.scope()
        val startedAt = cache.writeMarker()
        outbox?.retryNow()

        val permits = Semaphore(parallelism)
        val prefetched = coroutineScope {
            prefetch.map { read -> async { permits.withPermit { refreshOutcome { read() } } } }.awaitAll()
        }
        val stored = cache.getAllPersistent(scope)
            // Third-party pages (absolute URLs) aren't the backend's; they refresh when reopened.
            .filter { it.storedAtMillis < startedAt && !it.path.contains(ABSOLUTE_URL_MARKER) }
        val refetched = coroutineScope {
            stored.map { entry -> async { permits.withPermit { refetch(scope, entry) } } }.awaitAll()
        }

        val outcomes = prefetched + refetched
        return RinneCacheRefreshReport(
            refreshed = outcomes.count { it == RefreshOutcome.Refreshed },
            removed = outcomes.count { it == RefreshOutcome.Removed },
            failed = outcomes.count { it == RefreshOutcome.Failed },
        ).also { config.logger?.i(message = "Cache refresh: $it") }
    }

    private enum class RefreshOutcome { Refreshed, Removed, Failed }

    private suspend fun refetch(scope: String, entry: RinneStoredResponse): RefreshOutcome {
        val spec = RinneRequestSpec(method = RinneHttpMethod.Get, path = entry.path, parameters = entry.parameters)
        val maxAge = entry.expiresAtMillis
            ?.takeIf { it > entry.storedAtMillis }
            ?.let { (it - entry.storedAtMillis).milliseconds }
        val options = RinneCacheOptions(
            policy = RinneCachePolicy.NetworkOnly,
            maxAge = maxAge,
            persist = true,
            observe = false,
            tags = entry.tags,
        )
        return refreshOutcome(onGone = { cache.remove(entry.key) }) { cache.fetch(entry.key, scope, spec, options) }
    }

    private suspend fun refreshOutcome(onGone: suspend () -> Unit = {}, fetch: suspend () -> Unit): RefreshOutcome = try {
        fetch()
        RefreshOutcome.Refreshed
    } catch (e: CancellationException) {
        throw e
    } catch (e: RinneNetworkException.Http) {
        when (e.statusCode) {
            NOT_FOUND, GONE -> {
                onGone()
                RefreshOutcome.Removed
            }

            else -> RefreshOutcome.Failed
        }
    } catch (e: Throwable) {
        RefreshOutcome.Failed
    }

    /** Drops stored responses and the outbox (including unsent mutations) of [scope]. */
    suspend fun clearLocalData(scope: String = config.scope()) {
        cache.clear(scope)
        mutationStore?.clear(scope)
    }

    // endregion

    private companion object {
        const val INVALIDATIONS_BUFFER = 64
        const val DEFAULT_REFRESH_PARALLELISM = 4
        const val ABSOLUTE_URL_MARKER = "://"
        const val NOT_FOUND = 404
        const val GONE = 410
    }
}

inline fun <reified T> RinneClient.get(
    path: String,
    noinline block: RinneGetRequestBuilder.() -> Unit = {},
): Flow<RinneNetworkResult<T>> = get(typeOf<T>(), path, block)

suspend inline fun <reified R> RinneClient.post(
    path: String,
    noinline block: RinneMutationRequestBuilder.() -> Unit = {},
): RinneMutationResult<R> = mutate(RinneHttpMethod.Post, typeOf<R>(), path, block)

suspend inline fun <reified R> RinneClient.put(
    path: String,
    noinline block: RinneMutationRequestBuilder.() -> Unit = {},
): RinneMutationResult<R> = mutate(RinneHttpMethod.Put, typeOf<R>(), path, block)

suspend inline fun <reified R> RinneClient.patch(
    path: String,
    noinline block: RinneMutationRequestBuilder.() -> Unit = {},
): RinneMutationResult<R> = mutate(RinneHttpMethod.Patch, typeOf<R>(), path, block)

suspend inline fun <reified R> RinneClient.delete(
    path: String,
    noinline block: RinneMutationRequestBuilder.() -> Unit = {},
): RinneMutationResult<R> = mutate(RinneHttpMethod.Delete, typeOf<R>(), path, block)
