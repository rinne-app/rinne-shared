package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.offline.cache.RinneCachePolicy
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class RinneClientGetTest {

    @Test
    fun cacheAndNetworkEmitsStoredValueThenNetworkValue() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()
        server.items += Item("b", "B")

        val results = client.get<List<Item>>("items").takeUntilFinal()

        assertEquals(
            listOf(
                RinneNetworkResult.Data(listOf(Item("a", "A")), RinneDataSource.Cache, isFinal = false),
                RinneNetworkResult.Data(listOf(Item("a", "A"), Item("b", "B")), RinneDataSource.Network, isFinal = true),
            ),
            results,
        )
    }

    @Test
    fun activeObserverReceivesResultOfSomeoneElsesRequest() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        val observed = collectAll(client.get<List<Item>>("items"))
        runCurrent()
        assertEquals(listOf(Item("a", "A")), observed.lastValue)

        server.items += Item("b", "B")
        client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.NetworkOnly; persist = true } }.await()
        runCurrent()

        assertEquals(listOf(Item("a", "A"), Item("b", "B")), observed.lastValue)
    }

    @Test
    fun concurrentIdenticalRequestsShareOneNetworkCall() = runTest {
        val server = FakeServer().apply { responseDelayMillis = 100 }
        val client = testClient(server)

        val first = async { client.get<List<Item>>("items") { parameter("page", 1) }.await() }
        val second = async { client.get<List<Item>>("items") { parameter("page", 1) }.await() }

        assertEquals(first.await(), second.await())
        assertEquals(1, server.requests.size)
    }

    @Test
    fun queryParameterOrderDoesNotChangeTheCacheKey() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items") { parameter("a", 1); parameter("b", 2) }.await()
        server.goOffline()

        val cached = client.get<List<Item>>("items") {
            parameter("b", 2)
            parameter("a", 1)
            cache { policy = RinneCachePolicy.CacheOnly }
        }.await()

        assertEquals(listOf(Item("a", "A")), cached)
    }

    @Test
    fun networkFirstFallsBackToStoredValueWhenOffline() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()
        server.goOffline()

        val result = client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.NetworkFirst } }
            .first { it.isFinal }

        assertIs<RinneNetworkResult.Data<List<Item>>>(result)
        assertEquals(RinneDataSource.Cache, result.source)
        assertIs<RinneNetworkException.NoConnection>(result.refreshError)
    }

    @Test
    fun requestsFailFastWithoutTouchingTheTransportWhileOffline() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        server.goOffline()

        val result = client.get<List<Item>>("items").first { it.isFinal }

        assertIs<RinneNetworkResult.Error>(result)
        assertIs<RinneNetworkException.NoConnection>(result.cause)
        assertEquals(0, server.transportCalls)
    }

    @Test
    fun networkOnlyFailsWhenOfflineEvenWithStoredValue() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()
        server.goOffline()

        val result = client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.NetworkOnly } }
            .first { it.isFinal }

        assertIs<RinneNetworkResult.Error>(result)
    }

    @Test
    fun cacheOnlyWithNothingStoredIsACacheMiss() = runTest {
        val client = testClient(FakeServer())

        val result = client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.CacheOnly } }.first()

        assertIs<RinneNetworkResult.Error>(result)
        assertIs<RinneCacheMissException>(result.cause)
    }

    @Test
    fun cacheFirstSkipsNetworkWhileFresh() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()

        client.get<List<Item>>("items") { cache { policy = RinneCachePolicy.CacheFirst } }.await()

        assertEquals(1, server.requests.size)
    }

    @Test
    fun invalidatingATagRefetchesActiveObservers() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        val observed = collectAll(client.get<List<Item>>("items") { cache { tags("items") } })
        runCurrent()

        server.items += Item("b", "B")
        client.invalidate("items")
        runCurrent()

        assertEquals(2, server.requests.size)
        assertEquals(listOf(Item("a", "A"), Item("b", "B")), observed.lastValue)
    }

    @Test
    fun unchangedResponseIsRevalidatedWithETag() = runTest {
        val server = FakeServer()
        val client = testClient(server)
        client.get<List<Item>>("items").await()

        val revalidated = client.get<List<Item>>("items").await()

        assertEquals(listOf(Item("a", "A")), revalidated)
        assertEquals(server.requests[0].ifNoneMatch, null)
        assertTrue(server.requests[1].ifNoneMatch != null)
    }

    @Test
    fun nonObservingRequestCompletesAfterFinalValue() = runTest {
        val client = testClient(FakeServer())

        val results = client.get<List<Item>>("items") { cache { observe = false } }.toListForTest()

        assertTrue(results.last().isFinal)
    }
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<RinneNetworkResult<T>>.takeUntilFinal(): List<RinneNetworkResult<T>> {
    val results = mutableListOf<RinneNetworkResult<T>>()
    first {
        results += it
        it.isFinal
    }
    return results
}

private suspend fun <T> kotlinx.coroutines.flow.Flow<T>.toListForTest(): List<T> {
    val results = mutableListOf<T>()
    collect { results += it }
    return results
}
