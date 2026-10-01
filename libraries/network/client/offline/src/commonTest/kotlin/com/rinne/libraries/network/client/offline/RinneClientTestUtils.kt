package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import com.rinne.libraries.network.client.offline.store.InMemoryMutationStore
import com.rinne.libraries.network.client.offline.store.InMemoryResponseStore
import com.rinne.libraries.network.client.offline.store.RinneMutationStore
import com.rinne.libraries.network.client.offline.store.RinneResponseStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

internal fun TestScope.testClient(
    server: FakeServer,
    responseStore: RinneResponseStore = InMemoryResponseStore(),
    mutationStore: RinneMutationStore = InMemoryMutationStore(),
    configure: RinneClientConfig.() -> Unit = {},
) = RinneClient(server) {
    this.responseStore = responseStore
    this.mutationStore = mutationStore
    connectivity = server
    clock = { testScheduler.currentTime }
    coroutineScope = backgroundScope
    optimistic {
        on<List<Item>, NewItem>(RinneHttpMethod.Post, mutation = "items", target = "items") { current, mutation ->
            current.orEmpty() + Item(mutation.entityId!!, mutation.body!!.name)
        }
        on<List<Item>, ItemPatch>(RinneHttpMethod.Patch, mutation = "items/{id}", target = "items") { current, mutation ->
            current?.map { item ->
                when (item.id == mutation.pathParameters["id"]) {
                    true -> item.copy(name = mutation.body?.name ?: item.name)
                    false -> item
                }
            }
        }
        on<List<Item>, Unit>(RinneHttpMethod.Delete, mutation = "items/{id}", target = "items") { current, mutation ->
            current?.filterNot { it.id == mutation.pathParameters["id"] }
        }
        on<Item, NewItem>(RinneHttpMethod.Post, mutation = "items", target = "items/{id}") { current, mutation ->
            current ?: Item(mutation.entityId!!, mutation.body!!.name)
                .takeIf { it.id == mutation.targetParameters["id"] }
        }
    }
    configure()
}

/** Collects eagerly on the test scheduler and exposes everything emitted so far. */
@OptIn(ExperimentalCoroutinesApi::class)
internal fun <T> TestScope.collectAll(flow: Flow<T>): List<T> {
    val values = mutableListOf<T>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { flow.collect { values += it } }
    return values
}

internal val <T> List<RinneNetworkResult<T>>.lastValue: T?
    get() = lastOrNull()?.valueOrNull()
