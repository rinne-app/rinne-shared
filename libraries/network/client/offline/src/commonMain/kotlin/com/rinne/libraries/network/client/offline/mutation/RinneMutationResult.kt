package com.rinne.libraries.network.client.offline.mutation

internal data class RinneOfflineOptions(
    val queueIfOffline: Boolean,
    val createsEntity: Boolean,
    val idField: String,
    val invalidates: Set<String>,
)

sealed interface RinneMutationResult<out R> {
    /** Id of the created entity: the server id once sent, the temporary id while queued. */
    val entityId: String?

    /** The server applied the mutation. */
    data class Sent<out R>(val value: R, override val entityId: String?) : RinneMutationResult<R>

    /** Saved in the outbox and applied optimistically; it will be sent when the network allows. */
    data class Queued(
        val mutationId: String,
        override val entityId: String?,
        val cause: Throwable?,
    ) : RinneMutationResult<Nothing>

    /** Cancelled out by queued mutations (e.g. deleting an entity whose creation was never sent). */
    data object ResolvedLocally : RinneMutationResult<Nothing> {
        override val entityId: String? = null
    }
}
