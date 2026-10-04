package com.rinne.libraries.network.client.offline.mutation

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.e
import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.logger.core.extensions.w
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.core.isSuccessful
import com.rinne.libraries.network.client.core.model.RinneHttpHeader
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import com.rinne.libraries.network.client.core.model.asTextOrNull
import com.rinne.libraries.network.client.core.model.with
import com.rinne.libraries.network.client.offline.RinneRequestExecutor
import com.rinne.libraries.network.client.offline.asMethodName
import com.rinne.libraries.network.client.offline.asMimeType
import com.rinne.libraries.network.client.offline.connectivity.RinneConnectivity
import com.rinne.libraries.network.client.offline.request.RinneRequestSpec
import com.rinne.libraries.network.client.offline.rinneContentTypeOf
import com.rinne.libraries.network.client.offline.rinneHttpMethodOf
import com.rinne.libraries.network.client.offline.store.RinneMutationStatus
import com.rinne.libraries.network.client.offline.store.RinneMutationStore
import com.rinne.libraries.network.client.offline.store.RinnePendingMutation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Thrown for a queued mutation whose entity creation failed, so it can never be sent. */
class RinneDependencyFailedException(tempIds: Set<String>) :
    Exception("Depends on entities that were never created: $tempIds")

internal sealed interface RinneOutboxOutcome {
    data class Sent(val mutation: RinnePendingMutation, val response: RinneHttpResponse) : RinneOutboxOutcome
    data class Failed(val mutation: RinnePendingMutation, val cause: Throwable) : RinneOutboxOutcome
    data class Deferred(val mutation: RinnePendingMutation, val cause: Throwable?) : RinneOutboxOutcome
    data object ResolvedLocally : RinneOutboxOutcome
}

/**
 * Persistent FIFO queue of write requests. Mutations are sent strictly one at a time in order, so a
 * mutation that uses a temporary id always runs after the one that creates that entity.
 */
internal class RinneOutbox(
    private val store: RinneMutationStore,
    private val executor: RinneRequestExecutor,
    private val connectivity: RinneConnectivity,
    private val retryPolicy: RinneRetryPolicy,
    private val conflictResolver: RinneConflictResolver,
    private val sentRetentionMillis: Long,
    private val clock: () -> Long,
    private val scopeProvider: () -> String,
    private val onSent: suspend (RinnePendingMutation) -> Unit,
    private val coroutineScope: CoroutineScope,
    private val revisionField: String,
    private val logger: RinneLogger?,
) {
    private val queueMutex = Mutex()
    private val drainMutex = Mutex()
    private var inFlightId: String? = null
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = EVENTS_BUFFER)
    private val kicks = Channel<Unit>(Channel.CONFLATED)
    private var retryJob: Job? = null

    /**
     * Revisions the server moved past, per resource: an edit made against the old revision before
     * the refetch caught up is rebased onto the new one instead of hitting a false conflict.
     */
    private val revisionRebases = mutableMapOf<String, Map<String, String>>()

    private val syncingState = MutableStateFlow(false)
    private val lastErrorState = MutableStateFlow<Throwable?>(null)

    /** `true` while a mutation is on its way to the server. */
    val isSyncing: StateFlow<Boolean> = syncingState.asStateFlow()

    /** Why the queue last stopped or rejected a mutation; cleared by the next successful send. */
    val lastError: StateFlow<Throwable?> = lastErrorState.asStateFlow()

    private sealed interface Event {
        val mutationIds: Set<String>

        data class Sent(val mutation: RinnePendingMutation, val response: RinneHttpResponse) : Event {
            override val mutationIds = setOf(mutation.id)
        }

        data class Failed(val mutation: RinnePendingMutation, val cause: Throwable) : Event {
            override val mutationIds = setOf(mutation.id)
        }

        /** The queue stopped before reaching these mutations; they stay queued. */
        data class Blocked(override val mutationIds: Set<String>, val cause: Throwable?) : Event
    }

    init {
        coroutineScope.launch { kicks.receiveAsFlow().collect { drain() } }
        // Also replays whatever was left queued by the previous app session.
        coroutineScope.launch { connectivity.isOnline.filter { it }.collect { retryNow() } }
    }

    fun kick() {
        kicks.trySend(Unit)
    }

    /** The network is evidently reachable: skip the current backoff wait. */
    suspend fun retryNow() {
        queueMutex.withLock {
            val head = store.getAll(scopeProvider()).firstOrNull { it.status == RinneMutationStatus.Pending }
            if (head != null && head.nextAttemptAtMillis > 0) store.upsert(head.copy(nextAttemptAtMillis = 0))
        }
        kick()
    }

    /**
     * Stores the mutation (after coalescing) and waits until it is sent, fails, or the queue can't
     * make progress right now — whichever comes first.
     */
    suspend fun enqueueAndAwait(spec: RinneRequestSpec, options: RinneOfflineOptions): RinneOutboxOutcome =
        coroutineScope {
            val mutation = spec.asPendingMutation(options)
            val outcome = CompletableDeferred<Event>()
            // Subscribe before enqueueing so an immediate send can't be missed.
            val listener = launch(start = CoroutineStart.UNDISPATCHED) {
                events.collect { if (mutation.id in it.mutationIds) outcome.complete(it) }
            }

            val inserted = enqueue(mutation)
            if (inserted == null) {
                listener.cancel()
                return@coroutineScope RinneOutboxOutcome.ResolvedLocally
            }

            kick()
            val event = outcome.await()
            listener.cancel()
            when (event) {
                is Event.Sent -> RinneOutboxOutcome.Sent(event.mutation, event.response)
                is Event.Failed -> RinneOutboxOutcome.Failed(event.mutation, event.cause)
                is Event.Blocked -> RinneOutboxOutcome.Deferred(inserted, event.cause)
            }
        }

    suspend fun retry(id: String) {
        queueMutex.withLock {
            val mutation = store.getAll(scopeProvider()).firstOrNull { it.id == id } ?: return
            store.upsert(
                mutation.copy(
                    status = RinneMutationStatus.Pending,
                    attempts = 0,
                    nextAttemptAtMillis = 0,
                    failureCode = null,
                    failureMessage = null,
                )
            )
        }
        kick()
    }

    suspend fun discard(id: String) = queueMutex.withLock {
        if (id != inFlightId) store.delete(listOf(id))
    }

    private suspend fun enqueue(mutation: RinnePendingMutation): RinnePendingMutation? = queueMutex.withLock {
        val all = store.getAll(mutation.scope)
        val queued = all.filter { it.status == RinneMutationStatus.Pending && it.id != inFlightId }
        val sequenced = mutation.copy(sequence = (all.maxOfOrNull { it.sequence } ?: 0) + 1)
        val mappings = store.getIdMappings(mutation.scope)
        val plan = RinneMutationCoalescer.plan(sequenced.withRebasedRevision(mappings), queued, mappings)

        if (plan.removeIds.isNotEmpty()) store.delete(plan.removeIds)
        plan.insert?.let { store.upsert(it) }
        logger?.i(
            message = "Outbox: queued ${mutation.method} ${mutation.path}" +
                (if (plan.removeIds.isNotEmpty()) ", coalesced ${plan.removeIds.size}" else "") +
                (if (plan.insert == null) ", resolved locally" else ""),
        )
        plan.insert
    }

    private suspend fun drain() = drainMutex.withLock {
        val scope = scopeProvider()
        purgeSent(scope)

        try {
            while (true) {
                val head = queueMutex.withLock { nextToSend(scope) } ?: return@withLock
                syncingState.value = true
                val result = try {
                    Result.success(executor.execute(head.toSpec(store.getIdMappings(scope))))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    Result.failure(e)
                }
                // Resolved outside the queue lock: a resolver may itself be slow or touch the client.
                val conflict = result.getOrNull()
                    ?.takeIf { response -> response.status?.value?.let(RinneRetryPolicy::isConflictStatus) == true }
                    ?.let { response -> conflictResolver.resolve(head, response) }

                val keepGoing = queueMutex.withLock {
                    inFlightId = null
                    handleResult(head, result, conflict)
                }
                if (!keepGoing) return@withLock
            }
        } finally {
            syncingState.value = false
        }
    }

    /** Picks the head of the queue, or reports why the queue can't move. Called under [queueMutex]. */
    private suspend fun nextToSend(scope: String): RinnePendingMutation? {
        while (true) {
            val pending = store.getAll(scope).filter { it.status == RinneMutationStatus.Pending }
            val head = pending.firstOrNull() ?: return null

            if (!connectivity.isOnline.value) {
                lastErrorState.value = RinneNetworkException.NoConnection()
                emitBlocked(pending, RinneNetworkException.NoConnection())
                return null
            }
            if (head.nextAttemptAtMillis > clock()) {
                scheduleRetry(head.nextAttemptAtMillis)
                emitBlocked(pending, null)
                return null
            }

            val mappings = store.getIdMappings(scope)
            val missing = head.referencedTempIds() - mappings.keys
            if (missing.isNotEmpty()) {
                fail(head, RinneDependencyFailedException(missing))
                continue
            }

            inFlightId = head.id
            return head
        }
    }

    /** Returns whether the queue should continue with the next mutation. */
    private suspend fun handleResult(
        mutation: RinnePendingMutation,
        result: Result<RinneHttpResponse>,
        conflict: RinneConflictResolution?,
    ): Boolean {
        val response = result.getOrElse { return backOff(mutation, it) }
        val status = response.status?.value ?: return backOff(mutation, null)

        return when {
            response.isSuccessful -> {
                markSent(mutation, response)
                true
            }

            conflict != null -> resolveConflict(mutation, response, conflict)
            RinneRetryPolicy.isRetryableStatus(status) -> backOff(mutation, RinneNetworkException.Http(response))
            else -> {
                fail(mutation, RinneNetworkException.Http(response))
                true
            }
        }
    }

    private suspend fun markSent(mutation: RinnePendingMutation, response: RinneHttpResponse) {
        val serverId = mutation.tempId?.let { extractEntityId(response.body.asTextOrNull(), mutation.idField) }
        val sent = mutation.copy(
            status = RinneMutationStatus.Sent,
            sentAtMillis = clock(),
            serverId = serverId,
            attempts = mutation.attempts + 1,
        )
        if (mutation.tempId != null && serverId != null) store.putIdMapping(mutation.scope, mutation.tempId, serverId)
        store.upsert(sent)
        rebaseRevisions(mutation, response)
        lastErrorState.value = null
        logger?.i(message = "Outbox: sent ${mutation.method} ${mutation.path} after ${sent.attempts} attempt(s)")
        events.emit(Event.Sent(sent, response))
        coroutineScope.launch { onSent(sent) }
    }

    private suspend fun resolveConflict(
        mutation: RinnePendingMutation,
        response: RinneHttpResponse,
        resolution: RinneConflictResolution,
    ): Boolean {
        logger?.w(message = "Outbox: conflict on ${mutation.method} ${mutation.path} -> $resolution")
        if (mutation.attempts + 1 >= retryPolicy.maxAttempts) {
            fail(mutation, RinneNetworkException.Http(response))
            return true
        }
        val resolved = when (resolution) {
            RinneConflictResolution.Discard -> {
                fail(mutation, RinneNetworkException.Http(response))
                return true
            }

            RinneConflictResolution.Overwrite -> mutation.copy(headers = mutation.headers.withoutIfMatch())
            is RinneConflictResolution.Replace -> mutation.copy(
                method = resolution.method ?: mutation.method,
                path = resolution.path ?: mutation.path,
                body = resolution.body,
                headers = mutation.headers.withoutIfMatch() +
                    listOfNotNull(resolution.ifMatch?.let { IF_MATCH to it }),
            )
        }
        // A different request now: the server must not replay the stored conflict for the old key.
        store.upsert(resolved.copy(attempts = mutation.attempts + 1, idempotencyKey = randomId()))
        return true
    }

    /**
     * After a versioned write succeeds, queued writes of the same resource still carry the revision
     * it was based on; move them to the revision the server just returned.
     */
    private suspend fun rebaseRevisions(mutation: RinnePendingMutation, response: RinneHttpResponse) {
        val oldRevision = mutation.ifMatch() ?: return
        val newValue = extractEntityId(response.body.asTextOrNull(), revisionField) ?: return
        val newRevision = if (oldRevision.startsWith('"')) "\"$newValue\"" else newValue
        if (newRevision == oldRevision) return

        val mappings = store.getIdMappings(mutation.scope)
        val resource = mutation.resourceKey(mappings)
        revisionRebases[resource] = revisionRebases[resource].orEmpty() + (oldRevision to newRevision)
        store.getAll(mutation.scope)
            .filter { it.status == RinneMutationStatus.Pending && it.resourceKey(mappings) == resource }
            .forEach { queued -> queued.withRebasedRevision(mappings).takeIf { it != queued }?.let { store.upsert(it) } }
    }

    private fun RinnePendingMutation.withRebasedRevision(mappings: Map<String, String>): RinnePendingMutation {
        val rebases = revisionRebases[resourceKey(mappings)] ?: return this
        var revision = ifMatch() ?: return this
        while (true) revision = rebases[revision] ?: break
        return if (revision == ifMatch()) this else copy(headers = headers.withoutIfMatch() + (IF_MATCH to revision))
    }

    private fun RinnePendingMutation.resourceKey(mappings: Map<String, String>) =
        "$scope ${path.replaceTempIds(mappings).trim('/')}"

    private fun RinnePendingMutation.ifMatch(): String? =
        headers.entries.firstOrNull { it.key.equals(IF_MATCH, ignoreCase = true) }?.value

    private suspend fun backOff(mutation: RinnePendingMutation, cause: Throwable?): Boolean {
        val attempts = mutation.attempts + 1
        if (attempts >= retryPolicy.maxAttempts) {
            fail(mutation, cause ?: IllegalStateException("Gave up after $attempts attempts"))
            return true
        }

        val nextAttemptAt = clock() + retryPolicy.delayMillis(attempts)
        store.upsert(mutation.copy(attempts = attempts, nextAttemptAtMillis = nextAttemptAt))
        lastErrorState.value = cause
        logger?.w(
            message = "Outbox: ${mutation.method} ${mutation.path} attempt $attempts failed (${cause?.message}), " +
                "retrying in ${nextAttemptAt - clock()} ms",
        )
        scheduleRetry(nextAttemptAt)
        emitBlocked(store.getAll(mutation.scope).filter { it.status == RinneMutationStatus.Pending }, cause)
        return false
    }

    private suspend fun fail(mutation: RinnePendingMutation, cause: Throwable) {
        val failed = mutation.copy(
            status = RinneMutationStatus.Failed,
            attempts = mutation.attempts + 1,
            failureCode = (cause as? RinneNetworkException.Http)?.statusCode,
            failureMessage = cause.message,
        )
        store.upsert(failed)
        lastErrorState.value = cause
        logger?.e(message = "Outbox: ${mutation.method} ${mutation.path} rejected", throwable = cause)
        events.emit(Event.Failed(failed, cause))
    }

    private suspend fun emitBlocked(pending: List<RinnePendingMutation>, cause: Throwable?) {
        events.emit(Event.Blocked(pending.map { it.id }.toSet(), cause))
    }

    private fun scheduleRetry(atMillis: Long) {
        retryJob?.cancel()
        retryJob = coroutineScope.launch {
            delay((atMillis - clock()).coerceAtLeast(0))
            kick()
        }
    }

    /** Sent mutations only matter until the affected responses are refetched. */
    private suspend fun purgeSent(scope: String) = queueMutex.withLock {
        val expired = store.getAll(scope).filter {
            it.status == RinneMutationStatus.Sent && (it.sentAtMillis ?: 0) + sentRetentionMillis < clock()
        }
        if (expired.isNotEmpty()) store.delete(expired.map { it.id })
    }

    private fun RinneRequestSpec.asPendingMutation(options: RinneOfflineOptions): RinnePendingMutation {
        val (bodyText, contentType) = when (val content = body) {
            RinneOutgoingContent.Empty -> null to null
            is RinneOutgoingContent.Text -> content.text to content.contentType?.asMimeType()
            else -> throw IllegalArgumentException("Only text bodies can be queued offline, got $content")
        }

        return RinnePendingMutation(
            id = randomId(),
            scope = scopeProvider(),
            sequence = 0,
            method = method.asMethodName(),
            path = path,
            parameters = parameters,
            body = bodyText,
            contentType = contentType,
            headers = headers.headers
                .filterNot { it.name.equals(AUTHORIZATION, ignoreCase = true) }
                .associate { it.name to it.value },
            idempotencyKey = randomId(),
            tempId = if (options.createsEntity) generateTempId() else null,
            serverId = null,
            idField = options.idField,
            invalidates = options.invalidates,
            status = RinneMutationStatus.Pending,
            attempts = 0,
            nextAttemptAtMillis = 0,
            createdAtMillis = clock(),
            sentAtMillis = null,
            failureCode = null,
            failureMessage = null,
        )
    }

    private companion object {
        const val EVENTS_BUFFER = 64
        const val AUTHORIZATION = "Authorization"
        const val IF_MATCH = "If-Match"
    }
}

internal const val IDEMPOTENCY_KEY_HEADER = "Idempotency-Key"

/** Rebuilds the request with temporary ids swapped for server ids known by now. */
internal fun RinnePendingMutation.toSpec(mappings: Map<String, String>) = RinneRequestSpec(
    method = rinneHttpMethodOf(method),
    path = path.replaceTempIds(mappings),
    parameters = parameters,
    headers = RinneHttpHeaders.Custom(headers.map { (name, value) -> RinneHttpHeader.Default(name, value) })
        .with(IDEMPOTENCY_KEY_HEADER, idempotencyKey),
    body = when (body) {
        null -> RinneOutgoingContent.Empty
        else -> RinneOutgoingContent.Text(
            text = body.replaceTempIds(mappings),
            contentType = contentType?.let(::rinneContentTypeOf),
        )
    },
)

/** Temporary ids of other entities this mutation needs to exist on the server first. */
internal fun RinnePendingMutation.referencedTempIds(): Set<String> =
    (path.findTempIds() + body.orEmpty().findTempIds()) - setOfNotNull(tempId)

private fun Map<String, String>.withoutIfMatch() = filterKeys { !it.equals("If-Match", ignoreCase = true) }
