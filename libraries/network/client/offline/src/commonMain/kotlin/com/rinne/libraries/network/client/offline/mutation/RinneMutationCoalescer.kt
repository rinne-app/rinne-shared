package com.rinne.libraries.network.client.offline.mutation

import com.rinne.libraries.network.client.offline.store.RinnePendingMutation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Folds a new mutation into the queued ones before it is stored, so the server sees the minimal
 * sequence of writes. Only mutations that are not being sent right now may be touched.
 */
internal object RinneMutationCoalescer {

    data class Plan(
        /** `null` when the new mutation is cancelled out entirely. */
        val insert: RinnePendingMutation?,
        val removeIds: Set<String>,
    )

    fun plan(
        new: RinnePendingMutation,
        queued: List<RinnePendingMutation>,
        mappings: Map<String, String>,
    ): Plan {
        val newPath = new.path.resolved(mappings)
        val samePath = queued.filter { it.path.resolved(mappings) == newPath && it.parameters == new.parameters }

        return when (new.method) {
            DELETE -> planDelete(new, newPath, queued, samePath)
            PUT -> Plan(new, samePath.filter { it.method == PUT || it.method == PATCH }.ids())
            PATCH -> planPatch(new, samePath)
            else -> Plan(new, emptySet())
        }
    }

    private fun planDelete(
        new: RinnePendingMutation,
        newPath: String,
        queued: List<RinnePendingMutation>,
        samePath: List<RinnePendingMutation>,
    ): Plan {
        val deletedTempId = newPath.trim('/').substringAfterLast('/').takeIf { it.isTempId() }
        val unsentCreate = deletedTempId?.let { tempId -> queued.firstOrNull { it.tempId == tempId } }

        // The entity never reached the server: drop its creation and everything that touches it.
        if (unsentCreate != null && deletedTempId != null) {
            return Plan(insert = null, removeIds = queued.filter { it.references(deletedTempId) }.ids())
        }
        return Plan(new, samePath.filter { it.method == PUT || it.method == PATCH }.ids())
    }

    /** Two partial updates of the same resource become one; newer fields win. */
    private fun planPatch(new: RinnePendingMutation, samePath: List<RinnePendingMutation>): Plan {
        val previous = samePath.lastOrNull { it.method == PATCH } ?: return Plan(new, emptySet())
        val mergedBody = mergeJsonObjects(previous.body, new.body) ?: return Plan(new, emptySet())

        return Plan(
            insert = new.copy(
                body = mergedBody,
                // Keep the revision the first edit was based on.
                headers = new.headers + previous.headers.filterKeys { it.equals(IF_MATCH, ignoreCase = true) },
                invalidates = previous.invalidates + new.invalidates,
            ),
            removeIds = setOf(previous.id),
        )
    }

    private fun mergeJsonObjects(older: String?, newer: String?): String? {
        if (older == null || newer == null) return null
        val olderObject = runCatching { Json.parseToJsonElement(older) as? JsonObject }.getOrNull() ?: return null
        val newerObject = runCatching { Json.parseToJsonElement(newer) as? JsonObject }.getOrNull() ?: return null
        return JsonObject(olderObject + newerObject).toString()
    }

    private fun RinnePendingMutation.references(tempId: String) =
        this.tempId == tempId || path.contains(tempId) || body?.contains(tempId) == true

    private fun String.resolved(mappings: Map<String, String>) = replaceTempIds(mappings).trim('/')

    private fun List<RinnePendingMutation>.ids() = map { it.id }.toSet()

    private const val DELETE = "DELETE"
    private const val PUT = "PUT"
    private const val PATCH = "PATCH"
    private const val IF_MATCH = "If-Match"
}
