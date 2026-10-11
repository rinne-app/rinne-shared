package com.rinne.shared.strings

import kotlin.math.abs

/**
 * A locale's CLDR plural rules, as data: `{"one": "i = 1 and v = 0"}`. The same interpreter runs on
 * every platform, so every UI picks the same form, and a language added remotely brings its rules.
 *
 * Syntax (CLDR, Unicode TR35 "Language Plural Rules"): `or` / `and` of relations
 * `operand [mod|% value] (= | != | is [not] | [not] in | [not] within) value | range, …`; operands
 * `n i v w f t c e`; anything after `@` (the samples) is ignored.
 */
class PluralRules private constructor(private val rules: Map<PluralCategory, PluralRule>) {

    /** The categories this locale uses, `other` included. */
    val categories: Set<PluralCategory> = rules.keys + PluralCategory.Other

    /** The category of [count] (whole numbers: counts of things). */
    fun select(count: Long): PluralCategory = select(PluralOperands.of(count))

    fun select(operands: PluralOperands): PluralCategory =
        CategoryOrder.firstOrNull { category -> rules[category]?.matches(operands) == true } ?: PluralCategory.Other

    companion object {
        private val CategoryOrder = listOf(
            PluralCategory.Zero, PluralCategory.One, PluralCategory.Two, PluralCategory.Few, PluralCategory.Many,
        )

        /** Only `other`: every count takes the same form (e.g. Japanese). */
        val OtherOnly: PluralRules = PluralRules(emptyMap())

        /** @throws IllegalArgumentException for an unknown category or a rule that doesn't parse. */
        fun parse(rules: Map<String, String>): PluralRules = PluralRules(
            rules.filterKeys { it != PluralCategory.Other.cldrName }
                .entries.associate { (name, rule) ->
                    val category = requireNotNull(PluralCategory.fromCldrName(name)) { "Unknown plural category \"$name\"" }
                    category to PluralRule.parse(rule)
                },
        )
    }
}

/** The CLDR operands of a number. For whole numbers everything but `n` and `i` is 0. */
data class PluralOperands(
    val n: Double,
    val i: Long,
    val v: Int = 0,
    val w: Int = 0,
    val f: Long = 0,
    val t: Long = 0,
    val e: Int = 0,
) {
    companion object {
        fun of(count: Long): PluralOperands = abs(count).let { PluralOperands(n = it.toDouble(), i = it) }
    }
}

/** One parsed rule, e.g. `v = 0 and i % 10 = 2..4 and i % 100 != 12..14`. */
internal class PluralRule private constructor(private val condition: Condition) {

    fun matches(operands: PluralOperands): Boolean = condition.matches(operands)

    companion object {
        fun parse(rule: String): PluralRule {
            val text = rule.substringBefore('@').trim()
            require(text.isNotEmpty()) { "Empty plural rule" }
            return PluralRule(Parser(tokenize(text)).parseCondition())
        }

        private fun tokenize(text: String): List<String> {
            val tokens = mutableListOf<String>()
            var index = 0
            while (index < text.length) {
                val char = text[index]
                when {
                    char.isWhitespace() -> index++
                    text.startsWith("..", index) -> { tokens += ".."; index += 2 }
                    text.startsWith("!=", index) -> { tokens += "!="; index += 2 }
                    char == '=' || char == ',' || char == '%' -> { tokens += char.toString(); index++ }
                    char.isDigit() -> {
                        val end = (index until text.length).firstOrNull { !text[it].isDigit() } ?: text.length
                        tokens += text.substring(index, end)
                        index = end
                    }
                    char.isLetter() -> {
                        val end = (index until text.length).firstOrNull { !text[it].isLetter() } ?: text.length
                        tokens += text.substring(index, end)
                        index = end
                    }
                    else -> throw IllegalArgumentException("Unexpected \"$char\" in plural rule \"$text\"")
                }
            }
            return tokens
        }
    }

    private sealed interface Condition {
        fun matches(operands: PluralOperands): Boolean
    }

    private class Or(val parts: List<Condition>) : Condition {
        override fun matches(operands: PluralOperands) = parts.any { it.matches(operands) }
    }

    private class And(val parts: List<Condition>) : Condition {
        override fun matches(operands: PluralOperands) = parts.all { it.matches(operands) }
    }

    private class Relation(
        val operand: Char,
        val modulus: Long?,
        val negated: Boolean,
        val integerOnly: Boolean,
        val ranges: List<LongRange>,
    ) : Condition {
        override fun matches(operands: PluralOperands): Boolean {
            val value = operandValue(operands)
            val isInteger = value == kotlin.math.floor(value)
            val inRanges = ranges.any { range ->
                when (integerOnly) {
                    true -> isInteger && value.toLong() in range
                    false -> value >= range.first && value <= range.last
                }
            }
            return inRanges != negated
        }

        private fun operandValue(operands: PluralOperands): Double {
            val raw = when (operand) {
                'n' -> operands.n
                'i' -> operands.i.toDouble()
                'v' -> operands.v.toDouble()
                'w' -> operands.w.toDouble()
                'f' -> operands.f.toDouble()
                't' -> operands.t.toDouble()
                'c', 'e' -> operands.e.toDouble()
                else -> error("Unknown operand $operand")
            }
            return when (modulus) {
                null -> raw
                else -> raw % modulus
            }
        }
    }

    private class Parser(private val tokens: List<String>) {
        private var position = 0

        fun parseCondition(): Condition {
            val condition = parseOr()
            require(position == tokens.size) { "Unexpected \"${tokens[position]}\" in plural rule" }
            return condition
        }

        private fun parseOr(): Condition {
            val parts = mutableListOf(parseAnd())
            while (accept("or")) parts += parseAnd()
            return parts.singleOrNull() ?: Or(parts)
        }

        private fun parseAnd(): Condition {
            val parts = mutableListOf(parseRelation())
            while (accept("and")) parts += parseRelation()
            return parts.singleOrNull() ?: And(parts)
        }

        private fun parseRelation(): Condition {
            val operandToken = next()
            require(operandToken.length == 1 && operandToken[0] in "nivwftce") { "Unknown operand \"$operandToken\"" }
            val modulus = when (accept("mod") || accept("%")) {
                true -> nextNumber()
                false -> null
            }
            var negated = false
            var integerOnly = true
            when {
                accept("=") -> Unit
                accept("!=") -> negated = true
                accept("is") -> negated = accept("not")
                else -> {
                    negated = accept("not")
                    when {
                        accept("in") -> Unit
                        accept("within") -> integerOnly = false
                        else -> throw IllegalArgumentException("Expected a relation after \"$operandToken\"")
                    }
                }
            }
            return Relation(operandToken[0], modulus, negated, integerOnly, parseRanges())
        }

        private fun parseRanges(): List<LongRange> {
            val ranges = mutableListOf<LongRange>()
            do {
                val start = nextNumber()
                val end = when (accept("..")) {
                    true -> nextNumber()
                    false -> start
                }
                ranges += start..end
            } while (accept(","))
            return ranges
        }

        private fun accept(token: String): Boolean = when (tokens.getOrNull(position) == token) {
            true -> { position++; true }
            false -> false
        }

        private fun next(): String = tokens.getOrNull(position++)
            ?: throw IllegalArgumentException("Unexpected end of plural rule")

        private fun nextNumber(): Long = next().let { token ->
            requireNotNull(token.toLongOrNull()) { "Expected a number, got \"$token\"" }
        }
    }
}

/** Plural rules compiled into the app for its built-in languages: the fallback when a table has none. */
object BuiltInPluralRules {
    private val rules: Map<String, Map<String, String>> = mapOf(
        "en" to mapOf("one" to "i = 1 and v = 0"),
        "ru" to mapOf(
            "one" to "v = 0 and i % 10 = 1 and i % 100 != 11",
            "few" to "v = 0 and i % 10 = 2..4 and i % 100 != 12..14",
            "many" to "v = 0 and i % 10 = 0 or v = 0 and i % 10 = 5..9 or v = 0 and i % 100 = 11..14",
        ),
    )

    /** The rules of [locale] (`pt-BR` falls back to `pt`), or `null` when none are built in. */
    fun forLocale(locale: String): PluralRules? =
        (rules[locale] ?: rules[locale.substringBefore('-')])?.let(PluralRules::parse)
}
