package com.rinne.shared.strings

/** One locale's texts: what a table file (`tables/<locale>.json`) holds. */
class StringTable(
    val locale: String,
    /** The locale's CLDR plural rules carried by the table, if any (a remote language brings its own). */
    val pluralRules: PluralRules?,
    val values: Map<String, StringValue>,
) {
    operator fun get(key: RinneStringKey): StringValue? = values[key.id]

    override fun toString(): String = "StringTable($locale, ${values.size} texts)"
}

/** A text: one template, or one template per plural category. */
sealed interface StringValue {
    data class Plain(val template: String) : StringValue

    /** Plural forms by category; `other` is always present. */
    data class Plural(val forms: Map<PluralCategory, String>) : StringValue {
        init {
            require(PluralCategory.Other in forms) { "A plural needs an \"other\" form" }
        }
    }
}

/** CLDR plural categories. */
enum class PluralCategory(val cldrName: String) {
    Zero("zero"),
    One("one"),
    Two("two"),
    Few("few"),
    Many("many"),
    Other("other");

    companion object {
        fun fromCldrName(name: String): PluralCategory? = entries.firstOrNull { it.cldrName == name }
    }
}
