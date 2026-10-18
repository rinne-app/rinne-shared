package com.rinne.shared.network.model

import com.rinne.shared.network.model.tasks.NetworkTask
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskDueWireTest {
    @Test fun `a task due date survives wire decoding and encoding`() {
        val json = Json { ignoreUnknownKeys = true }
        val wire = """{"id":"task","title":"Read","createdAt":"2026-10-18T00:00:00Z","updatedAt":"2026-10-18T00:00:00Z","dueDate":"2026-10-19"}"""
        val task = json.decodeFromString(NetworkTask.serializer(), wire)
        val encoded = json.parseToJsonElement(json.encodeToString(NetworkTask.serializer(), task))
        assertEquals("2026-10-19", encoded.jsonObject["dueDate"]?.jsonPrimitive?.content)
    }
}
