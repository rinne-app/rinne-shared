package com.rinne.libraries.network.client.offline.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryResponseStore : RinneResponseStore {
    private val entries = MutableStateFlow<Map<String, RinneStoredResponse>>(emptyMap())

    override fun observe(key: String): Flow<RinneStoredResponse?> = entries.map { it[key] }.distinctUntilChanged()

    override suspend fun get(key: String): RinneStoredResponse? = entries.value[key]

    override suspend fun put(response: RinneStoredResponse) = entries.update { it + (response.key to response) }

    override suspend fun remove(key: String) = entries.update { it - key }

    override suspend fun markStale(scope: String, tags: Set<String>) = entries.update { all ->
        all.mapValues { (_, entry) ->
            when (entry.scope == scope && entry.tags.any { it in tags }) {
                true -> entry.copy(expiresAtMillis = 0)
                false -> entry
            }
        }
    }

    override suspend fun clear(scope: String) = entries.update { all -> all.filterValues { it.scope != scope } }
}

class InMemoryMutationStore : RinneMutationStore {
    private val mutations = MutableStateFlow<Map<String, RinnePendingMutation>>(emptyMap())
    private val idMappings = MutableStateFlow<Map<String, Map<String, String>>>(emptyMap())

    override fun observe(scope: String): Flow<List<RinnePendingMutation>> =
        mutations.map { it.inScope(scope) }.distinctUntilChanged()

    override suspend fun getAll(scope: String) = mutations.value.inScope(scope)

    override suspend fun upsert(mutation: RinnePendingMutation) = mutations.update { it + (mutation.id to mutation) }

    override suspend fun delete(ids: Collection<String>) = mutations.update { it - ids.toSet() }

    override suspend fun clear(scope: String) {
        mutations.update { all -> all.filterValues { it.scope != scope } }
        idMappings.update { it - scope }
    }

    override fun observeIdMappings(scope: String): Flow<Map<String, String>> =
        idMappings.map { it[scope].orEmpty() }.distinctUntilChanged()

    override suspend fun getIdMappings(scope: String) = idMappings.value[scope].orEmpty()

    override suspend fun putIdMapping(scope: String, tempId: String, serverId: String) = idMappings.update {
        it + (scope to (it[scope].orEmpty() + (tempId to serverId)))
    }

    private fun Map<String, RinnePendingMutation>.inScope(scope: String) =
        values.filter { it.scope == scope }.sortedBy { it.sequence }
}
