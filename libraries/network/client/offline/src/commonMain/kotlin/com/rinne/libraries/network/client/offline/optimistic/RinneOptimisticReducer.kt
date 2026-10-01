package com.rinne.libraries.network.client.offline.optimistic

import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import com.rinne.libraries.network.client.offline.store.RinneMutationStatus
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/** What a reducer knows about a queued (or just sent) mutation it applies to a GET value. */
data class RinneOptimisticMutation<out B>(
    val body: B?,
    /** Values captured by the mutation path pattern, e.g. `groupId`. Temporary ids are already resolved. */
    val pathParameters: Map<String, String>,
    /** Values captured by the target (GET) path pattern. */
    val targetParameters: Map<String, String>,
    /** Id of the entity the mutation creates: the server id once known, the temporary id before. */
    val entityId: String?,
    val status: RinneMutationStatus,
    val createdAtMillis: Long,
)

/**
 * Applies mutations of [method] + [mutationPattern] to GET responses of [targetPattern] typed as
 * [targetType]. Registered globally (not per call) so queued mutations are still applied after an
 * app restart.
 */
class RinneOptimisticReducer<T, B> @PublishedApi internal constructor(
    internal val targetPattern: RinnePathPattern,
    internal val targetType: KType,
    internal val method: RinneHttpMethod,
    internal val mutationPattern: RinnePathPattern,
    internal val bodyType: KType,
    internal val reduce: (current: T?, mutation: RinneOptimisticMutation<B>) -> T?,
)

class RinneOptimisticReducersBuilder @PublishedApi internal constructor() {
    @PublishedApi
    internal val reducers = mutableListOf<RinneOptimisticReducer<*, *>>()

    /**
     * @param target GET path pattern whose value is updated, e.g. `tasks`.
     * @param mutation path pattern of the write, e.g. `tasks/group/{groupId}/task`.
     * @param reduce returns the value as it will look once the server applies the mutation.
     *   `current` is `null` when nothing is stored for the target yet.
     */
    inline fun <reified T, reified B> on(
        method: RinneHttpMethod,
        mutation: String,
        target: String,
        noinline reduce: (current: T?, mutation: RinneOptimisticMutation<B>) -> T?,
    ) {
        reducers += RinneOptimisticReducer(
            targetPattern = RinnePathPattern(target),
            targetType = typeOf<T>(),
            method = method,
            mutationPattern = RinnePathPattern(mutation),
            bodyType = typeOf<B>(),
            reduce = reduce,
        )
    }
}
