package com.rinne.shared.network.model

import com.rinne.shared.network.model.notes.NetworkNote
import com.rinne.shared.network.model.notes.NetworkNoteFolder
import com.rinne.shared.network.model.sync.SyncField
import com.rinne.shared.network.model.sync.SyncFieldValue
import com.rinne.shared.network.model.sync.readSyncField
import com.rinne.shared.network.model.sync.syncFields
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SyncFieldsTest {

    private fun assertFieldsOf(descriptor: SerialDescriptor, fields: List<SyncField<*>>) {
        val properties = (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }.toSet()
        fields.forEach { field -> assertTrue(field.name in properties, "${field.name} is not a property of ${descriptor.serialName}") }
    }

    @Test
    fun `every sync field is a property of its DTO`() {
        assertFieldsOf(NetworkNote.serializer().descriptor, NetworkNote.Fields.All)
        assertFieldsOf(NetworkNoteFolder.serializer().descriptor, NetworkNoteFolder.Fields.All)
    }

    @Test
    fun `fields round-trip through the builder and the reader`() {
        val fields = syncFields {
            set(NetworkNote.Fields.Title, "Plan")
            set(NetworkNote.Fields.FolderId, null)
        }
        assertEquals(SyncFieldValue.Present("Plan"), fields.readSyncField(NetworkNote.Fields.Title))
        assertEquals(SyncFieldValue.Present(null), fields.readSyncField(NetworkNote.Fields.FolderId))
        assertEquals(SyncFieldValue.Absent, fields.readSyncField(NetworkNote.Fields.Content))
        val wrongType = JsonObject(mapOf("title" to JsonObject(emptyMap())))
        assertIs<SyncFieldValue.Invalid>(wrongType.readSyncField(NetworkNote.Fields.Title))
    }
}
