package com.rinne.shared.strings

/**
 * The tables built into the app (from `tables/`): the base layer of every lookup, available offline
 * and on first start. English defines the keys; the other locales may miss some.
 */
object BundledStringTables {

    /** The locales built in, e.g. `en`, `ru`. */
    val locales: Set<String> get() = GeneratedBundledTables.all.keys

    private val tables: Map<String, StringTable> by lazy {
        GeneratedBundledTables.all.mapValues { (_, json) -> StringTableParser.parse(json) }
    }

    /** The built-in table of [locale], or `null`. */
    fun table(locale: String): StringTable? = tables[locale]

    /** The built-in table of [locale] as the JSON file it was built from (what the backend serves). */
    fun json(locale: String): String? = GeneratedBundledTables.all[locale]

    val english: StringTable get() = tables.getValue("en")

    /** Every key's placeholder signature, from the English table. */
    val signatures: Map<String, PlaceholderSignature> by lazy {
        GeneratedSignatures.all.mapValues { (_, template) -> PlaceholderSignature.of(template) }
    }
}
