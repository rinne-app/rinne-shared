package com.rinne.shared.strings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PluralRulesTest {

    private val english = PluralRules.parse(mapOf("one" to "i = 1 and v = 0 @integer 1"))
    private val russian = BuiltInPluralRules.forLocale("ru")!!

    @Test
    fun `english has one and other`() {
        assertEquals(PluralCategory.One, english.select(1))
        assertEquals(PluralCategory.Other, english.select(0))
        assertEquals(PluralCategory.Other, english.select(2))
        assertEquals(PluralCategory.Other, english.select(11))
    }

    @Test
    fun `russian picks one and few and many`() {
        val expected = mapOf(
            1L to PluralCategory.One, 21L to PluralCategory.One, 101L to PluralCategory.One,
            2L to PluralCategory.Few, 4L to PluralCategory.Few, 22L to PluralCategory.Few,
            0L to PluralCategory.Many, 5L to PluralCategory.Many, 11L to PluralCategory.Many,
            12L to PluralCategory.Many, 14L to PluralCategory.Many, 111L to PluralCategory.Many,
        )
        expected.forEach { (count, category) -> assertEquals(category, russian.select(count), "count $count") }
    }

    @Test
    fun `negative counts use their absolute value`() {
        assertEquals(PluralCategory.One, english.select(-1))
    }

    @Test
    fun `relations support every CLDR form`() {
        val rules = PluralRules.parse(
            mapOf(
                "zero" to "n = 0",
                "two" to "n is 2",
                "few" to "n mod 10 in 3..4, 9 and n not within 10..19",
                "many" to "n % 100 != 0..2 and n is not 50",
            ),
        )
        assertEquals(PluralCategory.Zero, rules.select(0))
        assertEquals(PluralCategory.Two, rules.select(2))
        assertEquals(PluralCategory.Few, rules.select(3))
        assertEquals(PluralCategory.Few, rules.select(29))
        assertEquals(PluralCategory.Many, rules.select(13))
        assertEquals(PluralCategory.Other, rules.select(50))
        assertEquals(PluralCategory.Other, rules.select(100))
    }

    @Test
    fun `an invalid rule is rejected`() {
        assertFailsWith<IllegalArgumentException> { PluralRules.parse(mapOf("one" to "x = 1")) }
        assertFailsWith<IllegalArgumentException> { PluralRules.parse(mapOf("one" to "n = ")) }
        assertFailsWith<IllegalArgumentException> { PluralRules.parse(mapOf("several" to "n = 1")) }
    }

    @Test
    fun `built-in rules fall back from region to language`() {
        assertEquals(PluralCategory.Few, BuiltInPluralRules.forLocale("ru-RU")!!.select(3))
    }
}
