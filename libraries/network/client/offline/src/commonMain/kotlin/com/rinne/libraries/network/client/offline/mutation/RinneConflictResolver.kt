package com.rinne.libraries.network.client.offline.mutation

import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.offline.store.RinnePendingMutation

/** Decides what to do when a queued mutation hits 409/412 (the entity changed since the edit was made). */
fun interface RinneConflictResolver {
    suspend fun resolve(mutation: RinnePendingMutation, response: RinneHttpResponse): RinneConflictResolution

    companion object {
        /** Keep the server version; the local change is reported through the failed mutations. */
        val Discard = RinneConflictResolver { _, _ -> RinneConflictResolution.Discard }
    }
}

sealed interface RinneConflictResolution {
    /** Drop the local change. */
    data object Discard : RinneConflictResolution

    /** Resend without the precondition (last write wins). */
    data object Overwrite : RinneConflictResolution

    /**
     * Send a different request instead: a body merged with the server state (optionally against a
     * new revision), or another endpoint altogether — e.g. a POST that saves the local edit as a copy.
     * `null` [method]/[path] keep the original ones.
     */
    data class Replace(
        val body: String?,
        val ifMatch: String? = null,
        val method: String? = null,
        val path: String? = null,
    ) : RinneConflictResolution
}
