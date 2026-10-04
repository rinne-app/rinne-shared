package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.offline.mutation.RinneConflictResolution
import com.rinne.libraries.network.client.offline.mutation.RinneConflictResolver
import com.rinne.libraries.network.client.offline.mutation.RinneMutationResult
import com.rinne.libraries.network.client.offline.mutation.isTempId
import com.rinne.libraries.network.client.offline.store.InMemoryMutationStore
import com.rinne.libraries.network.client.offline.store.RinneMutationStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RinneClientMutationTest {

    private suspend fun RinneClient.createItem(name: String) = post<Item>("items") {
        setBody(NewItem(name))
        offline {
            queueIfOffline = true
            createsEntity()
            invalidates("items")
        }
    }

    private suspend fun RinneClient.renameItem(id: String, name: String) = patch<Unit>("items/$id") {
        setBody(ItemPatch(name = name))
        offline {
            queueIfOffline = true
            invalidates("items")
        }
    }

    private suspend fun RinneClient.deleteItem(id: String) = delete<Unit>("items/$id") {
        offline {
            queueIfOffline = true
            invalidates("items")
        }
    }

    @Test
    fun onlineQueuedMutationIsSentAndReturnsServerEntity() = runTest {
        val server = FakeServer()
        val client = testClient(server)

        val result = client.createItem("B")

        assertEquals(RinneMutationResult.Sent(Item("srv1", "B"), entityId = "srv1"), result)
    }

    @Test
    fun offlineMutationsAreQueuedAppliedOptimisticallyAndReplayedWithServerIds() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        val observed = collectAll(client.get<List<Item>>("items") { cache { tags("items") } })
        runCurrent()
        server.goOffline()

        val created = client.createItem("B")
        assertIs<RinneMutationResult.Queued>(created)
        val tempId = created.entityId!!
        assertTrue(tempId.isTempId())
        client.renameItem(tempId, "B2")
        runCurrent()

        val optimistic = observed.last()
        assertIs<RinneNetworkResult.Data<List<Item>>>(optimistic)
        assertEquals(listOf(Item("a", "A"), Item(tempId, "B2")), optimistic.value)
        assertTrue(optimistic.hasPendingMutations)

        server.goOnline()
        runCurrent()

        val writes = server.requests.filter { it.method != "GET" }
        assertEquals(listOf("POST items", "PATCH items/srv1"), writes.map { "${it.method} ${it.path}" })
        assertEquals(listOf(Item("a", "A"), Item("srv1", "B2")), server.items)
        val final = observed.last()
        assertIs<RinneNetworkResult.Data<List<Item>>>(final)
        assertEquals(listOf(Item("a", "A"), Item("srv1", "B2")), final.value)
        assertEquals(false, final.hasPendingMutations)
    }

    @Test
    fun requestWithTemporaryIdFollowsTheEntityOnceSynced() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()
        val tempId = client.createItem("B").entityId!!

        val observed = collectAll(client.get<Item>("items/$tempId"))
        runCurrent()
        assertEquals(Item(tempId, "B"), observed.lastValue)

        server.goOnline()
        runCurrent()

        assertEquals(Item("srv1", "B"), observed.lastValue)
        assertTrue(server.requests.any { it.method == "GET" && it.path == "items/srv1" })
    }

    @Test
    fun deletingAnUnsentEntityCancelsEverythingQueuedForIt() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()
        val tempId = client.createItem("B").entityId!!
        client.renameItem(tempId, "B2")

        assertEquals(RinneMutationResult.ResolvedLocally, client.deleteItem(tempId))
        server.goOnline()
        runCurrent()

        assertEquals(emptyList(), server.requests)
        assertEquals(emptyList(), client.observeMutations().first())
    }

    @Test
    fun consecutivePatchesOfOneResourceAreMerged() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()
        client.renameItem("a", "first")
        client.patch<Unit>("items/a") {
            setBody(ItemPatch(description = "desc"))
            offline { queueIfOffline = true }
        }

        server.goOnline()
        runCurrent()

        val patches = server.requests.filter { it.method == "PATCH" }
        assertEquals(1, patches.size)
        assertEquals("""{"name":"first","description":"desc"}""", patches.single().body)
    }

    @Test
    fun rejectedCreationRevertsOptimisticStateAndFailsDependents() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        val observed = collectAll(client.get<List<Item>>("items"))
        runCurrent()
        server.goOffline()
        val tempId = client.createItem("B").entityId!!
        client.renameItem(tempId, "B2")

        server.failures += 400
        server.goOnline()
        runCurrent()

        assertEquals(listOf(Item("a", "A")), observed.lastValue)
        val failed = client.observeMutations().first()
        assertEquals(listOf(RinneMutationStatus.Failed, RinneMutationStatus.Failed), failed.map { it.status })
        assertEquals(400, failed[0].failureCode)
        assertTrue(failed[1].failureMessage!!.contains(tempId))
        assertEquals(1, server.requests.count { it.method != "GET" })
    }

    @Test
    fun serverErrorsAreRetriedWithBackoffAndTheSameIdempotencyKey() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.failures += 500

        val result = client.createItem("B")
        assertIs<RinneMutationResult.Queued>(result)

        advanceTimeBy(RETRY_WAIT_MILLIS)
        runCurrent()

        val posts = server.requests.filter { it.method == "POST" }
        assertEquals(2, posts.size)
        assertEquals(posts[0].idempotencyKey, posts[1].idempotencyKey)
        assertEquals(listOf(Item("a", "A"), Item("srv1", "B")), server.items)
    }

    @Test
    fun directMutationFailsImmediatelyWhenOffline() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()

        assertFailsWith<RinneNetworkException.NoConnection> {
            client.post<Item>("items") { setBody(NewItem("B")) }
        }
    }

    @Test
    fun queuedMutationsSurviveAClientRestart() = runTest {
        val server = FakeServer()
        val mutationStore = InMemoryMutationStore()
        server.goOffline()
        testClient(server, mutationStore = mutationStore).createItem("B")

        val restarted = testClient(server, mutationStore = mutationStore)
        server.goOnline()
        runCurrent()

        assertEquals(listOf(Item("a", "A"), Item("srv1", "B")), server.items)
        assertEquals(emptyList(), restarted.observeMutations().first())
    }

    @Test
    fun anyResponseSkipsTheBackoffWait() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.failures += 503
        client.createItem("B")

        client.get<List<Item>>("items").await()
        runCurrent()

        assertEquals(2, server.requests.count { it.method == "POST" })
    }

    @Test
    fun conflictIsDiscardedByDefault() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.failures += 409

        assertFailsWith<RinneNetworkException.Http> { client.renameVersioned("a", "B", revision = 1) }

        assertEquals(409, client.observeMutations().first().single().failureCode)
    }

    @Test
    fun overwriteResolutionResendsWithoutPrecondition() = runTest {
        val server = FakeServer()
        val client = testClient(server) { conflictResolver = RinneConflictResolver { _, _ -> RinneConflictResolution.Overwrite } }
        server.failures += 409

        client.renameVersioned("a", "B", revision = 1)

        val patches = server.requests.filter { it.method == "PATCH" }
        assertEquals(listOf("\"1\"", null), patches.map { it.ifMatch })
        assertEquals("B", server.items.single().name)
    }

    @Test
    fun conflictResendUsesANewIdempotencyKey() = runTest {
        val server = FakeServer()
        val client = testClient(server) { conflictResolver = RinneConflictResolver { _, _ -> RinneConflictResolution.Overwrite } }
        server.failures += 409

        client.renameVersioned("a", "B", revision = 1)

        val keys = server.requests.filter { it.method == "PATCH" }.map { it.idempotencyKey }
        assertEquals(2, keys.toSet().size)
    }

    @Test
    fun conflictCanBeReplacedByAnotherRequest() = runTest {
        val server = FakeServer()
        val client = testClient(server) {
            conflictResolver = RinneConflictResolver { _, _ ->
                RinneConflictResolution.Replace(body = """{"name":"B (copy)"}""", method = "POST", path = "items")
            }
        }
        server.failures += 409

        client.renameVersioned("a", "B", revision = 1)

        assertEquals(listOf(Item("a", "A"), Item("srv1", "B (copy)")), server.items)
    }

    @Test
    fun queuedEditOfTheSameResourceIsRebasedOntoTheNewRevision() = runTest {
        val server = FakeServer().apply { responseDelayMillis = 100 }
        val client = testClient(server)

        val first = async { client.renameVersioned("a", "B", revision = 1) }
        runCurrent()
        // Made while the first edit is in flight, against the revision the UI still shows.
        val second = async { client.renameVersioned("a", "C", revision = 1) }
        first.await()
        second.await()

        assertEquals(listOf("\"1\"", "\"2\""), server.requests.filter { it.method == "PATCH" }.map { it.ifMatch })
        assertEquals("C", server.items.single().name)
    }

    @Test
    fun editMadeAfterTheFirstWasSentIsRebasedToo() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.renameVersioned("a", "B", revision = 1)

        // The UI hasn't refetched yet and still sends revision 1.
        client.renameVersioned("a", "C", revision = 1)

        assertEquals(listOf("\"1\"", "\"2\""), server.requests.filter { it.method == "PATCH" }.map { it.ifMatch })
    }

    @Test
    fun syncStateTracksTheOutbox() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()
        client.createItem("B")

        assertEquals(1, client.observeSyncState().first().pendingCount)

        server.goOnline()
        runCurrent()

        val synced = client.observeSyncState().first()
        assertTrue(synced.isSynced)
        assertEquals(null, synced.lastError)
    }

    private suspend fun RinneClient.renameVersioned(id: String, name: String, revision: Long) = patch<Unit>("items/$id") {
        setBody(ItemPatch(name = name))
        ifMatch(revision)
        offline { queueIfOffline = true }
    }

    private companion object {
        const val RETRY_WAIT_MILLIS = 3_000L
    }
}

