package com.rinne.libraries.network.client.offline.request

import com.rinne.libraries.date.time.core.RinneDuration
import com.rinne.libraries.network.client.core.RinneHttpClientConfigSettings
import com.rinne.libraries.network.client.core.model.RinneHttpHeaders
import com.rinne.libraries.network.client.core.model.RinneHttpMethod
import com.rinne.libraries.network.client.core.model.RinneOutgoingContent
import com.rinne.libraries.network.client.core.model.with
import com.rinne.libraries.network.client.offline.RinneBodyCodec
import com.rinne.libraries.network.client.offline.cache.RinneCacheOptions
import com.rinne.libraries.network.client.offline.cache.RinneCachePolicy
import com.rinne.libraries.network.client.offline.mutation.RinneOfflineOptions
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@DslMarker
annotation class RinneRequestDsl

@RinneRequestDsl
sealed class RinneRequestBuilder(@PublishedApi internal val codec: RinneBodyCodec) {
    private val parameters = mutableMapOf<String, MutableList<String>>()
    private var headers: RinneHttpHeaders = RinneHttpHeaders.Empty
    private var body: RinneOutgoingContent = RinneOutgoingContent.Empty
    private var timeouts: RinneHttpClientConfigSettings.Timeouts? = null

    /** Adds a query parameter; `null` values are skipped, like Ktor's `parameter`. */
    fun parameter(name: String, value: Any?) {
        if (value != null) parameters.getOrPut(name) { mutableListOf() }.add(value.toString())
    }

    fun header(name: String, value: String) {
        headers = headers.with(name, value)
    }

    inline fun <reified B> setBody(value: B) = setBody(value, typeOf<B>())

    fun setBody(value: Any?, type: KType) {
        body = codec.encode(value, type)
    }

    /** Overrides the client's timeouts for this request only, e.g. for slow AI endpoints. */
    fun timeout(
        requestTimeoutMillis: Long? = null,
        connectTimeoutMillis: Long? = null,
        socketTimeoutMillis: Long? = null,
    ) {
        timeouts = RinneHttpClientConfigSettings.Timeouts(
            socketTimeoutMillis = socketTimeoutMillis,
            requestTimeoutMillis = requestTimeoutMillis,
            connectTimeoutMillis = connectTimeoutMillis,
        )
    }

    internal fun buildSpec(method: RinneHttpMethod, path: String) = RinneRequestSpec(
        method = method,
        path = path,
        parameters = parameters.mapValues { it.value.toList() },
        headers = headers,
        body = body,
        timeouts = timeouts,
    )
}

class RinneGetRequestBuilder internal constructor(
    codec: RinneBodyCodec,
    private val defaultPolicy: RinneCachePolicy,
) : RinneRequestBuilder(codec) {
    private var cacheBuilder = RinneCacheOptionsBuilder(defaultPolicy)

    fun cache(block: RinneCacheOptionsBuilder.() -> Unit) {
        cacheBuilder = RinneCacheOptionsBuilder(defaultPolicy).apply(block)
    }

    internal fun buildCacheOptions() = cacheBuilder.build()
}

class RinneMutationRequestBuilder internal constructor(codec: RinneBodyCodec) : RinneRequestBuilder(codec) {
    private var offlineBuilder = RinneOfflineOptionsBuilder()

    fun offline(block: RinneOfflineOptionsBuilder.() -> Unit) {
        offlineBuilder = RinneOfflineOptionsBuilder().apply(block)
    }

    /**
     * Optimistic concurrency: the server applies the change only if the entity is still at
     * [revision], otherwise it answers 409/412 and the client's conflict resolver decides.
     */
    fun ifMatch(revision: Any) = header(IF_MATCH, "\"$revision\"")

    internal fun buildOfflineOptions() = offlineBuilder.build()

    internal companion object {
        const val IF_MATCH = "If-Match"
    }
}

@RinneRequestDsl
class RinneCacheOptionsBuilder internal constructor(var policy: RinneCachePolicy) {
    /** How long a stored response counts as fresh for [RinneCachePolicy.CacheFirst]; `null` = forever. */
    var maxAge: RinneDuration? = null

    /** Whether the response survives app restarts. Defaults to `false` only for [RinneCachePolicy.NetworkOnly]. */
    var persist: Boolean? = null

    /** Keep emitting later updates of this request (other callers, invalidations) after the final value. */
    var observe: Boolean = true

    private val tags = mutableSetOf<String>()

    /** Tags that mutations can invalidate (see [RinneOfflineOptionsBuilder.invalidates]). */
    fun tags(vararg tags: String) {
        this.tags += tags
    }

    internal fun build() = RinneCacheOptions(
        policy = policy,
        maxAge = maxAge,
        persist = persist ?: (policy != RinneCachePolicy.NetworkOnly),
        observe = observe,
        tags = tags.toSet(),
    )
}

@RinneRequestDsl
class RinneOfflineOptionsBuilder internal constructor() {
    /**
     * Put the mutation into the persistent outbox when it can't be sent now, and replay it later.
     * Opt-in: requests that are really queries (AI generation, search) must not be replayed.
     */
    var queueIfOffline: Boolean = false

    private var createsEntity = false
    private var idField = DEFAULT_ID_FIELD
    private val invalidates = mutableSetOf<String>()

    /**
     * The mutation creates an entity whose id the server assigns. While queued the entity gets a
     * temporary id that later requests may use; it is swapped for the real id read from [idField]
     * of the response (or the plain-text response itself).
     */
    fun createsEntity(idField: String = DEFAULT_ID_FIELD) {
        createsEntity = true
        this.idField = idField
    }

    /** Cache tags to mark stale and refetch once the mutation reaches the server. */
    fun invalidates(vararg tags: String) {
        invalidates += tags
    }

    internal fun build() = RinneOfflineOptions(
        queueIfOffline = queueIfOffline,
        createsEntity = createsEntity,
        idField = idField,
        invalidates = invalidates.toSet(),
    )

    private companion object {
        const val DEFAULT_ID_FIELD = "id"
    }
}
