package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.offline.cache.RinneCachePolicy
import com.rinne.libraries.network.client.offline.cache.RinneCacheRefreshReport
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class RinneClientRefreshTest {

    @Test
    fun everyStoredResponseIsRefetchedWithItsQuery() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items") { parameter("page", 2) }.await()
        client.get<Item>("items/a").await()
        server.items[0] = Item("a", "Renamed")
        server.requests.clear()

        val report = client.refreshCache()

        assertEquals(RinneCacheRefreshReport(refreshed = 2, removed = 0, failed = 0), report)
        assertEquals(setOf("items", "items/a"), server.requests.map { it.path }.toSet())
        val cached = client.get<Item>("items/a") { cache { policy = RinneCachePolicy.CacheOnly } }.await()
        assertEquals("Renamed", cached.name)
    }

    @Test
    fun observersReceiveTheRefreshedValues() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        val observed = collectAll(client.get<List<Item>>("items"))
        runCurrent()
        server.items += Item("b", "B")

        client.refreshCache()
        runCurrent()

        assertEquals(listOf(Item("a", "A"), Item("b", "B")), observed.lastValue)
    }

    @Test
    fun responsesTheServerNoLongerHasAreDropped() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<Item>("items/a").await()
        server.items.clear()

        val report = client.refreshCache()

        assertEquals(1, report.removed)
        val result = client.get<Item>("items/a") { cache { policy = RinneCachePolicy.CacheOnly } }.first()
        assertIs<RinneNetworkResult.Error>(result)
    }

    @Test
    fun prefetchedReadsAreStoredAndNotFetchedTwice() = runTest {
        val server = FakeServer()
        val client = testClient(server)

        val report = client.refreshCache(prefetch = listOf({ client.get<List<Item>>("items").awaitFresh() }))

        assertEquals(1, report.refreshed)
        assertEquals(1, server.requests.size)
        assertEquals(listOf(Item("a", "A")), client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.CacheOnly } }.await())
    }

    @Test
    fun aFailedPrefetchIsCountedEvenWithAStoredFallback() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()
        server.failures += 500

        val report = client.refreshCache(prefetch = listOf({ client.get<List<Item>>("items").awaitFresh() }))

        assertEquals(1, report.failed)
    }

    @Test
    fun refreshingOfflineFailsRightAway() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()

        assertFailsWith<RinneNetworkException.NoConnection> { client.refreshCache() }
    }
}
