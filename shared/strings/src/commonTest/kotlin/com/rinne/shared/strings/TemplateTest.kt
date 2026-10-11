package com.rinne.shared.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TemplateTest {

    @Test
    fun `positional placeholders are substituted`() {
        assertEquals("Anna has 3 notes", Template.format("%1\$s has %2\$d notes", listOf("Anna", "3")))
        assertEquals("3 notes of Anna", Template.format("%2\$d notes of %1\$s", listOf("Anna", "3")))
    }

    @Test
    fun `sequential placeholders and a percent sign`() {
        assertEquals("a b 100%", Template.format("%s %s 100%%", listOf("a", "b")))
    }

    @Test
    fun `the signature lists positions and types`() {
        assertEquals(
            PlaceholderSignature(mapOf(1 to PlaceholderType.Text, 2 to PlaceholderType.Number)),
            PlaceholderSignature.of("%1\$s has %2\$d notes"),
        )
    }

    @Test
    fun `a signature accepts a subset with the same types`() {
        val full = PlaceholderSignature.of("%1\$d notes")
        assertTrue(full.accepts(PlaceholderSignature.Empty))
        assertFalse(full.accepts(PlaceholderSignature.of("%1\$s notes")))
        assertFalse(full.accepts(PlaceholderSignature.of("%2\$d notes")))
    }

    @Test
    fun `malformed placeholders are rejected`() {
        assertFailsWith<IllegalArgumentException> { PlaceholderSignature.of("100%") }
        assertFailsWith<IllegalArgumentException> { PlaceholderSignature.of("%1\$x") }
        assertFailsWith<IllegalArgumentException> { PlaceholderSignature.of("%1\$s and %1\$d") }
    }
}
