package com.rinne.shared.network.model

import com.rinne.shared.network.model.attachments.NetworkAttachment
import com.rinne.shared.network.model.notes.NetworkNote
import com.rinne.shared.network.model.notes.NetworkNoteFolder
import com.rinne.shared.network.model.scopes.NetworkScope
import com.rinne.shared.network.model.scopes.NetworkScopeItem
import com.rinne.shared.network.model.scopes.NetworkScopeSchedule
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
        assertFieldsOf(NetworkAttachment.serializer().descriptor, NetworkAttachment.Fields.All)
        assertFieldsOf(NetworkScope.serializer().descriptor, NetworkScope.Fields.All)
    }

    @Test
    fun `nested values round-trip as fields`() {
        val items = listOf(NetworkScopeItem(NetworkScopeItem.AppNotes, "folder-1"))
        val schedule = NetworkScopeSchedule(enabled = true, days = listOf(1, 2, 3, 4, 5), startMinute = 540, endMinute = 1080)
        val fields = syncFields {
            set(NetworkScope.Fields.Items, items)
            set(NetworkScope.Fields.Schedule, schedule)
        }
        assertEquals(SyncFieldValue.Present(items), fields.readSyncField(NetworkScope.Fields.Items))
        assertEquals(SyncFieldValue.Present(schedule), fields.readSyncField(NetworkScope.Fields.Schedule))
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
