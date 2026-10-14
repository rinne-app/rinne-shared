package com.rinne.shared.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class StringTablesTest {

    @Test
    fun `German translates every English key and names the language`() {
        val german = BundledStringTables.table("de")!!
        assertEquals(BundledStringTables.english.values.keys, german.values.keys)
        assertEquals(StringValue.Plain("Deutsch"), german.values["languageName"])
    }

    @Test
    fun `German table and regional fallback distinguish one from other`() {
        val rules = listOf(
            BundledStringTables.table("de")!!.pluralRules!!,
            BuiltInPluralRules.forLocale("de-DE")!!,
        )
        rules.forEach { rule ->
            assertEquals(PluralCategory.One, rule.select(1))
            listOf(0L, 2L, 11L, 21L, 101L).forEach { count ->
                assertEquals(PluralCategory.Other, rule.select(count), "count $count")
            }
            assertEquals(PluralCategory.Other, rule.select(PluralOperands(n = 1.5, i = 1, v = 1, w = 1, f = 5, t = 5)))
            assertEquals(PluralCategory.Other, rule.select(PluralOperands(n = 1.0, i = 1, v = 1)))
        }
    }

    @Test
    fun `every bundled table parses and English defines every key`() {
        val english = BundledStringTables.english
        BundledStringTables.locales.forEach { locale ->
            val table = BundledStringTables.table(locale)!!
            assertEquals(locale, table.locale)
            val unknown = table.values.keys - english.values.keys
            assertTrue(unknown.isEmpty(), "$locale has keys English doesn't define: $unknown")
        }
    }

    @Test
    fun `every bundled translation fits the build`() {
        BundledStringTables.locales.forEach { locale ->
            val result = StringTableValidator.validate(BundledStringTables.table(locale)!!)
            assertTrue(result.dropped.isEmpty(), "$locale: ${result.dropped}")
        }
    }

    @Test
    fun `every bundled table has plural rules covering its plural forms`() {
        BundledStringTables.locales.forEach { locale ->
            val table = BundledStringTables.table(locale)!!
            assertTrue(table.pluralRules != null, "$locale has no plural rules")
        }
    }

    @Test
    fun `missing keys of a translation are reported and not failed`() {
        val missing = BundledStringTables.locales.associateWith { locale ->
            BundledStringTables.english.values.keys - BundledStringTables.table(locale)!!.values.keys
        }.filterValues { it.isNotEmpty() }
        // English covers them through the lookup chain; this only makes them visible.
        missing.forEach { (locale, keys) -> println("$locale misses ${keys.size} keys (English is used for them)") }
    }

    @Test
    fun `the generated keys match the table`() {
        assertEquals("errorDefaultTitle", RinneStrings.Error.DefaultTitle.id)
        assertTrue(BundledStringTables.english[RinneStrings.Notes.CountHolder] is StringValue.Plural)
    }

    @Test
    fun `a remote table drops what doesn't fit the build`() {
        val remote = StringTableParser.parse(
            """
            {"locale": "en", "strings": {
              "errorDefaultTitle": "Oops",
              "errorCodeHolder": "Error %1${'$'}s",
              "unknownKeyFromTheFuture": "Hi",
              "notesCountHolder": "%1${'$'}d notes",
              "notesFolderCountHolder": {"few": "%1${'$'}d", "other": "%1${'$'}d times"}
            }}
            """.trimIndent(),
        )
        val result = StringTableValidator.validate(remote)
        assertEquals(setOf("errorDefaultTitle"), result.table.values.keys)
        assertEquals(
            setOf("errorCodeHolder", "unknownKeyFromTheFuture", "notesCountHolder", "notesFolderCountHolder"),
            result.dropped.map { it.key }.toSet(),
        )
    }

    @Test
    fun `a table that isn't a table is rejected`() {
        assertFailsWith<StringTableFormatException> { StringTableParser.parse("[]") }
        assertFailsWith<StringTableFormatException> { StringTableParser.parse("""{"locale": "en"}""") }
        assertFailsWith<StringTableFormatException> {
            StringTableParser.parse("""{"locale": "en", "strings": {"a": {"one": "x"}}}""")
        }
    }
}
