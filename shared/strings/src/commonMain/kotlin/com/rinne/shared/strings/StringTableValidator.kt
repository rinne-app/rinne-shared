package com.rinne.shared.strings

/**
 * Checks a table — typically one downloaded at runtime — against what this build knows, and drops
 * every entry it couldn't show safely: a key the build doesn't have, a text with other placeholders
 * than the English one, a plain text where the build expects plural forms (or the other way round),
 * or plural forms that don't fit the locale's plural rules. A dropped entry falls back to the next
 * table in the lookup chain, so a bad download can't crash a format call or show a broken text.
 */
object StringTableValidator {

    data class Result(val table: StringTable, val dropped: List<Dropped>)

    data class Dropped(val key: String, val reason: String)

    fun validate(
        table: StringTable,
        reference: StringTable = BundledStringTables.english,
        signatures: Map<String, PlaceholderSignature> = BundledStringTables.signatures,
        fallbackPluralRules: PluralRules? = BuiltInPluralRules.forLocale(table.locale),
    ): Result {
        val pluralRules = table.pluralRules ?: fallbackPluralRules ?: PluralRules.OtherOnly
        val dropped = mutableListOf<Dropped>()
        val kept = table.values.filter { (key, value) ->
            val problem = problemOf(key, value, reference.values[key], signatures[key], pluralRules)
            if (problem != null) dropped += Dropped(key, problem)
            problem == null
        }
        return Result(StringTable(table.locale, table.pluralRules, kept), dropped)
    }

    private fun problemOf(
        key: String,
        value: StringValue,
        referenceValue: StringValue?,
        signature: PlaceholderSignature?,
        pluralRules: PluralRules,
    ): String? {
        if (referenceValue == null || signature == null) return "unknown key"
        val templates = when (value) {
            is StringValue.Plain -> when (referenceValue) {
                is StringValue.Plain -> listOf(value.template)
                is StringValue.Plural -> return "expected plural forms"
            }
            is StringValue.Plural -> when (referenceValue) {
                is StringValue.Plain -> return "expected a plain text"
                is StringValue.Plural -> {
                    val unknown = value.forms.keys - pluralRules.categories
                    if (unknown.isNotEmpty()) return "plural forms ${unknown.map { it.cldrName }} don't exist in this locale"
                    value.forms.values.toList()
                }
            }
        }
        return templates.firstNotNullOfOrNull { template ->
            val templateSignature = try {
                PlaceholderSignature.of(template)
            } catch (exception: IllegalArgumentException) {
                return@firstNotNullOfOrNull "malformed placeholder: ${exception.message}"
            }
            when {
                !signature.accepts(templateSignature) -> "placeholders differ from the English text"
                value is StringValue.Plain && templateSignature != signature -> "placeholders differ from the English text"
                else -> null
            }
        }
    }
}
