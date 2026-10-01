package com.rinne.libraries.network.client.offline

import com.rinne.libraries.network.client.core.RinneHttpClient
import com.rinne.libraries.network.client.core.RinneHttpRequest
import com.rinne.libraries.network.client.core.RinneHttpResponse
import com.rinne.libraries.network.client.core.RinneNetworkException
import com.rinne.libraries.network.client.core.RinneUrl
import com.rinne.libraries.network.client.core.model.RinneHttpHeader
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.network.client.core.model.RinneHttpStatusCode
import com.rinne.libraries.network.client.core.model.RinneIncomingContent
import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import com.rinne.libraries.network.client.core.model.get
import com.rinne.libraries.network.client.offline.connectivity.RinneConnectivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Item(val id: String, val name: String)

@Serializable
data class NewItem(val name: String)

@Serializable
data class ItemPatch(val name: String? = null, val description: String? = null)

data class RecordedRequest(
    val method: String,
    val path: String,
    val body: String?,
    val idempotencyKey: String?,
    val ifMatch: String? = null,
    val ifNoneMatch: String? = null,
)

/** In-process fake of a REST backend for `items`, plus a switchable network. */
class FakeServer : RinneHttpClient, RinneConnectivity {
    override val isOnline = MutableStateFlow(true)
    val items = mutableListOf(Item("a", "A"))
    val requests = mutableListOf<RecordedRequest>()

    /** Every call that reached the transport, including ones that failed for lack of network. */
    var transportCalls = 0
    var responseDelayMillis = 0L

    /** Statuses to answer with before handling normally, consumed one per request. */
    val failures = ArrayDeque<Int>()
    private var nextId = 1

    fun goOffline() {
        isOnline.value = false
    }

    fun goOnline() {
        isOnline.value = true
    }

    override suspend fun callRequest(request: RinneHttpRequest): RinneHttpResponse {
        transportCalls++
        if (!isOnline.value) throw RinneNetworkException.NoConnection()
        if (responseDelayMillis > 0) delay(responseDelayMillis)

        val path = (request.url as RinneUrl.Address).url.substringBefore('?').trim('/')
        val method = request.method.asMethodName()
        val body = (request.body as? RinneOutgoingContent.Text)?.text
        requests += RecordedRequest(
            method = method,
            path = path,
            body = body,
            idempotencyKey = request.headers["Idempotency-Key"],
            ifMatch = request.headers["If-Match"],
            ifNoneMatch = request.headers["If-None-Match"],
        )

        failures.removeFirstOrNull()?.let { return request.respond(it, "") }

        val segments = path.split('/')
        return when {
            method == "GET" && path == "items" -> {
                val etag = "\"${items.hashCode()}\""
                when (request.headers["If-None-Match"] == etag) {
                    true -> request.respond(304, "", etag)
                    false -> request.respond(200, Json.encodeToString(items.toList()), etag)
                }
            }
            method == "GET" && segments.size == 2 -> items.firstOrNull { it.id == segments[1] }
                ?.let { request.respond(200, Json.encodeToString(it)) }
                ?: request.respond(404, "")

            method == "POST" && path == "items" -> {
                val created = Item("srv${nextId++}", Json.decodeFromString<NewItem>(body!!).name)
                items += created
                request.respond(201, Json.encodeToString(created))
            }

            method == "PATCH" && segments.size == 2 -> {
                val index = items.indexOfFirst { it.id == segments[1] }
                if (index == -1) return request.respond(404, "")
                val patch = Json.decodeFromString<ItemPatch>(body!!)
                items[index] = items[index].copy(name = patch.name ?: items[index].name)
                request.respond(200, "")
            }

            method == "DELETE" && segments.size == 2 -> {
                items.removeAll { it.id == segments[1] }
                request.respond(200, "")
            }

            else -> request.respond(404, "")
        }
    }

    private fun RinneHttpRequest.respond(status: Int, body: String, etag: String? = null) = RinneHttpResponse(
        request = this,
        status = RinneHttpStatusCode(status, ""),
        headers = etag?.let { RinneHttpHeaders.Custom(listOf(RinneHttpHeader.Default("ETag", it))) } ?: RinneHttpHeaders.Empty,
        body = if (body.isEmpty()) RinneIncomingContent.Empty else RinneIncomingContent.Text(body),
    )
}
