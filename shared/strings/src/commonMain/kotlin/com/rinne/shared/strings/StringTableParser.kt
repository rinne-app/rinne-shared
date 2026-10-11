package com.rinne.shared.strings

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

/** A table file that isn't a valid table. */
class StringTableFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Parses a table file:
 *
 * ```json
 * { "locale": "en", "pluralRules": { "one": "i = 1 and v = 0" },
 *   "strings": { "errorDefaultTitle": "Something went wrong", "notesCountHolder": { "one": "%1$d note", "other": "%1$d notes" } } }
 * ```
 */
object StringTableParser {

    /** @throws StringTableFormatException when [json] isn't a table. Entries are not validated here. */
    fun parse(json: String): StringTable {
        val root = try {
            Json.parseToJsonElement(json) as? JsonObject
        } catch (exception: Exception) {
            throw StringTableFormatException("Not JSON: ${exception.message}", exception)
        } ?: throw StringTableFormatException("The root must be an object")

        val locale = root.string("locale") ?: throw StringTableFormatException("\"locale\" is missing")
        val pluralRules = (root["pluralRules"] as? JsonObject)?.let { rules ->
            try {
                PluralRules.parse(rules.mapValues { (category, rule) ->
                    (rule as? JsonPrimitive)?.contentOrNull
                        ?: throw StringTableFormatException("Plural rule \"$category\" must be a string")
                })
            } catch (exception: IllegalArgumentException) {
                throw StringTableFormatException("Invalid plural rules: ${exception.message}", exception)
            }
        }
        val strings = root["strings"] as? JsonObject ?: throw StringTableFormatException("\"strings\" is missing")
        return StringTable(
            locale = locale,
            pluralRules = pluralRules,
            values = strings.mapValues { (key, value) -> parseValue(key, value) },
        )
    }

    private fun parseValue(key: String, value: kotlinx.serialization.json.JsonElement): StringValue = when (value) {
        is JsonPrimitive -> StringValue.Plain(
            value.contentOrNull?.takeIf { value.isString } ?: throw StringTableFormatException("\"$key\" must be a string"),
        )
        is JsonObject -> {
            val forms = value.entries.associate { (category, form) ->
                val pluralCategory = PluralCategory.fromCldrName(category)
                    ?: throw StringTableFormatException("\"$key\": unknown plural category \"$category\"")
                val template = (form as? JsonPrimitive)?.takeIf { it.isString }?.contentOrNull
                    ?: throw StringTableFormatException("\"$key\".$category must be a string")
                pluralCategory to template
            }
            when (PluralCategory.Other in forms) {
                true -> StringValue.Plural(forms)
                false -> throw StringTableFormatException("\"$key\": a plural needs an \"other\" form")
            }
        }
        else -> throw StringTableFormatException("\"$key\" must be a string or an object of plural forms")
    }

    private fun JsonObject.string(name: String): String? = (this[name] as? JsonPrimitive)?.contentOrNull
}
