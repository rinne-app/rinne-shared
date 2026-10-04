package com.rinne.libraries.network.client.offline.optimistic

import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.e
import com.rinne.libraries.network.client.offline.RinneBodyCodec
import com.rinne.libraries.network.client.offline.asMethodName
import com.rinne.libraries.network.client.offline.mutation.replaceTempIds
import com.rinne.libraries.network.client.offline.request.RinneRequestSpec
import com.rinne.libraries.network.client.offline.store.RinneMutationStatus
import com.rinne.libraries.network.client.offline.store.RinnePendingMutation
import kotlin.reflect.KType

internal data class RinneOverlayResult<T>(val value: T?, val hasPendingMutations: Boolean)

/**
 * Computes what a GET value looks like with queued mutations applied, without ever writing that
 * state to the store: the stored response stays the server's truth and the overlay is recomputed on
 * top of it, so a rejected mutation simply stops being applied.
 */
internal class RinneOptimisticOverlay(
    private val reducers: List<RinneOptimisticReducer<*, *>>,
    private val codec: RinneBodyCodec,
) {
    fun hasReducersFor(type: KType, path: String) = reducers.any { it.targets(type, path) }

    fun <T> apply(
        base: T?,
        type: KType,
        target: RinneRequestSpec,
        baseStoredAtMillis: Long?,
        mutations: List<RinnePendingMutation>,
        mappings: Map<String, String>,
    ): RinneOverlayResult<T> {
        val path = target.path
        val candidates = reducers.filter { it.targets(type, path) }
        if (candidates.isEmpty() || mutations.isEmpty()) return RinneOverlayResult(base, false)

        var value = base
        var hasPending = false
        mutations.filter { it.appliesOnTopOf(baseStoredAtMillis) }.forEach { mutation ->
            val mutationPath = mutation.path.replaceTempIds(mappings)
            candidates.forEach { reducer ->
                val applied = reducer.applyTo(value, target, mutation, mutationPath, mappings) ?: return@forEach
                value = applied.value
                hasPending = hasPending || mutation.status == RinneMutationStatus.Pending
            }
        }
        return RinneOverlayResult(value, hasPending)
    }

    /**
     * Pending mutations always apply; sent ones only until the stored value was fetched after them,
     * which avoids flicker between "server accepted" and "cache refetched".
     */
    private fun RinnePendingMutation.appliesOnTopOf(baseStoredAtMillis: Long?) = when (status) {
        RinneMutationStatus.Pending -> true
        RinneMutationStatus.Sent -> baseStoredAtMillis == null || baseStoredAtMillis < (sentAtMillis ?: 0)
        RinneMutationStatus.Failed -> false
    }

    private fun RinneOptimisticReducer<*, *>.targets(type: KType, path: String) =
        targetType == type && targetPattern.match(path) != null

    @Suppress("UNCHECKED_CAST")
    private fun <T> RinneOptimisticReducer<*, *>.applyTo(
        current: T?,
        target: RinneRequestSpec,
        mutation: RinnePendingMutation,
        mutationPath: String,
        mappings: Map<String, String>,
    ): RinneOverlayResult<T>? {
        if (method.asMethodName() != mutation.method) return null
        val pathParameters = mutationPattern.match(mutationPath) ?: return null
        val targetParameters = targetPattern.match(target.path) ?: return null

        return try {
            val body = mutation.body?.let { codec.decode(it.replaceTempIds(mappings), bodyType) }
            val optimisticMutation = RinneOptimisticMutation(
                body = body,
                pathParameters = pathParameters,
                targetParameters = targetParameters,
                targetQuery = target.parameters,
                mutationQuery = mutation.parameters,
                entityId = mutation.serverId ?: mutation.tempId?.let { mappings[it] ?: it },
                status = mutation.status,
                createdAtMillis = mutation.createdAtMillis,
            )
            val typedReduce = reduce as (T?, RinneOptimisticMutation<Any?>) -> T?
            RinneOverlayResult(typedReduce(current, optimisticMutation), true)
        } catch (e: Exception) {
            // A broken reducer must not hide the server data.
            RinneLogger.e(message = "Optimistic reducer failed for ${mutation.method} ${mutation.path}", throwable = e)
            null
        }
    }
}
