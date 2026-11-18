package com.rinne.shared.network.model

import com.rinne.shared.network.model.ai.AiErrorCodes
import com.rinne.shared.network.model.ai.NetworkAiMemorizerFieldTarget
import com.rinne.shared.network.model.ai.NetworkAiMemorizerFieldsRequest
import com.rinne.shared.network.model.ai.NetworkAiMemorizerFieldsResponse
import com.rinne.shared.network.model.memorizer.NetworkMemorizer
import com.rinne.shared.network.model.memorizer.NetworkMemorizerCard
import com.rinne.shared.network.model.memorizer.NetworkMemorizerField
import com.rinne.shared.network.model.memorizer.NetworkMemorizerReview
import com.rinne.shared.network.model.memorizer.NetworkMemorizerSet
import com.rinne.shared.network.model.memorizer.NetworkMemorizerSettings
import com.rinne.shared.network.model.scopes.NetworkScopeItem
import com.rinne.shared.network.model.sync.SyncFieldValue
import com.rinne.shared.network.model.sync.readSyncField
import com.rinne.shared.network.model.sync.syncFields
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MemorizerWireTest {
    private val json = Json { ignoreUnknownKeys = true }
    private val stamps = """"createdAt":"2026-11-18T00:00:00Z","updatedAt":"2026-11-18T00:00:00Z""""

    @Test fun `a minimal set decodes with the defaults`() {
        val set = json.decodeFromString(NetworkMemorizerSet.serializer(), """{"id":"s","name":"Español",$stamps}""")
        assertEquals(emptyList(), set.fields)
        assertEquals(NetworkMemorizerSettings(), set.settings)
        assertFalse(set.pinned)
        assertEquals(set, json.decodeFromString(NetworkMemorizerSet.serializer(), json.encodeToString(NetworkMemorizerSet.serializer(), set)))
    }

    @Test fun `a field's reverse switch round-trips`() {
        val field = NetworkMemorizerField(id = "example", name = "Example", reverse = true)
        val encoded = json.encodeToString(NetworkMemorizerField.serializer(), field)
        assertEquals(true, encoded.contains("\"reverse\":true"))
        assertEquals(field, json.decodeFromString(NetworkMemorizerField.serializer(), encoded))
    }

    @Test fun `a field decodes with its defaults and fields and settings round-trip as sync fields`() {
        val field = json.decodeFromString(NetworkMemorizerField.serializer(), """{"id":"example","name":"Example"}""")
        assertEquals(NetworkMemorizerField.Kinds.Text, field.kind)
        assertEquals(NetworkMemorizerField.Sides.Back, field.side)
        assertFalse(field.required)
        assertFalse(field.reverse)
        val settings = NetworkMemorizerSettings(newPerDay = 10, retention = 0.95, learningSteps = listOf(60, 600))
        val fields = syncFields {
            set(NetworkMemorizerSet.Fields.CardFields, listOf(field))
            set(NetworkMemorizerSet.Fields.Settings, settings)
            set(NetworkMemorizerSet.Fields.FolderId, null)
        }
        assertEquals(SyncFieldValue.Present(listOf(field)), fields.readSyncField(NetworkMemorizerSet.Fields.CardFields))
        assertEquals(SyncFieldValue.Present(settings), fields.readSyncField(NetworkMemorizerSet.Fields.Settings))
        assertEquals(SyncFieldValue.Present(null), fields.readSyncField(NetworkMemorizerSet.Fields.FolderId))
    }

    @Test fun `settings left out follow the defaults`() {
        val settings = json.decodeFromString(NetworkMemorizerSettings.serializer(), """{"reverse":true}""")
        assertEquals(true, settings.reverse)
        assertNull(settings.newPerDay)
        assertNull(settings.learningSteps)
    }

    @Test fun `a card and a review decode`() {
        val card = json.decodeFromString(
            NetworkMemorizerCard.serializer(),
            """{"id":"c","setId":"s",$stamps,"values":{"front":"la manzana","back":"the apple"}}""",
        )
        assertEquals("the apple", card.values[NetworkMemorizerField.Ids.Back])
        assertNull(card.level)
        val review = json.decodeFromString(
            NetworkMemorizerReview.serializer(),
            """{"id":"r","cardId":"c",$stamps,"direction":"forward","kind":"rate","reviewedAt":"2026-11-18T09:00:00Z",""" +
                """"due":"2026-11-19T09:00:00Z","state":"review","grade":3,"stability":3.17,"difficulty":5.3}""",
        )
        assertEquals(3, review.grade)
        assertEquals(0, review.lapses)
        assertEquals(review, json.decodeFromString(NetworkMemorizerReview.serializer(), json.encodeToString(NetworkMemorizerReview.serializer(), review)))
    }

    @Test fun `the fixed sets name every value the wire allows`() {
        assertEquals(listOf("front", "back"), NetworkMemorizerField.Ids.Fixed)
        assertEquals(listOf("text", "long_text", "audio", "image", "link"), NetworkMemorizerField.Kinds.All)
        assertEquals(listOf("front", "back"), NetworkMemorizerField.Sides.All)
        assertEquals(listOf("forward", "reverse"), NetworkMemorizerReview.Directions.All)
        assertEquals(listOf("rate", "postpone", "reset", "skip"), NetworkMemorizerReview.Kinds.All)
        assertEquals(listOf("new", "learning", "review", "relearning"), NetworkMemorizerReview.States.All)
        assertEquals("memorizer_set", NetworkMemorizer.KindSet)
        assertEquals("memorizer_card", NetworkMemorizer.KindCard)
        assertEquals("memorizer", NetworkScopeItem.AppMemorizer)
    }

    @Test fun `the AI request and response have the plan's shape`() {
        val request = NetworkAiMemorizerFieldsRequest(
            values = mapOf("front" to "la manzana"),
            targets = listOf(NetworkAiMemorizerFieldTarget(id = "back", name = "Back")),
            frontLanguage = "es",
            backLanguage = "en",
            setName = "Español",
        )
        val wire = json.encodeToString(NetworkAiMemorizerFieldsRequest.serializer(), request)
        assertEquals(request, json.decodeFromString(NetworkAiMemorizerFieldsRequest.serializer(), wire))
        val response = json.decodeFromString(NetworkAiMemorizerFieldsResponse.serializer(), """{"values":{"back":"the apple"}}""")
        assertEquals("the apple", response.values["back"])
        assertEquals(listOf("ai_unavailable", "ai_rate_limited", "ai_failed"), listOf(AiErrorCodes.Unavailable, AiErrorCodes.RateLimited, AiErrorCodes.Failed))
    }
}
