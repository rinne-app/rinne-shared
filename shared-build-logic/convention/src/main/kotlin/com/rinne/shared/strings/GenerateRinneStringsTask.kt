package com.rinne.shared.strings

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Generates the Kotlin side of the string tables in `rinne-shared/shared/strings/tables`:
 *
 * - `RinneStrings` — every key of `en.json` as a constant, grouped by prefix
 *   (`errorDefaultTitle` → `RinneStrings.Error.DefaultTitle`); a missing key doesn't compile;
 * - the placeholder signature of every key (`%1$s %2$d` → text, number), which remote tables are
 *   validated against;
 * - the bundled tables themselves, compiled into the code as the base layer every app ships;
 * - the texts the operating system reads itself (keys starting with `os`): Android `strings.xml`
 *   and Apple `InfoPlist.strings`, per locale.
 */
@CacheableTask
abstract class GenerateRinneStringsTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val tablesDirectory: DirectoryProperty

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val kotlinOutputDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val osTextsOutputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val tables = tablesDirectory.get().asFile.listFiles { file -> file.extension == "json" }
            .orEmpty()
            .sortedBy { it.name }
            .associate { it.nameWithoutExtension to it }
        val english = tables["en"] ?: throw GradleException("en.json is missing: it defines the keys")
        val keys = readStrings(english)

        val kotlinDir = kotlinOutputDirectory.get().asFile.apply { deleteRecursively(); mkdirs() }
        val packageDir = File(kotlinDir, packageName.get().replace('.', '/')).apply { mkdirs() }
        File(packageDir, "RinneStrings.kt").writeText(keysSource(keys.keys))
        File(packageDir, "GeneratedSignatures.kt").writeText(signaturesSource(keys))
        File(packageDir, "GeneratedBundledTables.kt").writeText(bundledTablesSource(tables))

        val osDir = osTextsOutputDirectory.get().asFile.apply { deleteRecursively(); mkdirs() }
        tables.forEach { (locale, file) -> writeOsTexts(osDir, locale, readStrings(file)) }
    }

    private fun readStrings(file: File): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val root = JsonSlurper().parse(file) as? Map<String, Any>
            ?: throw GradleException("${file.name}: the root must be an object")
        @Suppress("UNCHECKED_CAST")
        return (root["strings"] as? Map<String, Any>)
            ?: throw GradleException("${file.name}: \"strings\" must be an object")
    }

    private fun keysSource(keys: Set<String>): String {
        val groups = keys.groupBy { prefixOf(it) }.toSortedMap()
        return buildString {
            appendLine(HEADER)
            appendLine("package ${packageName.get()}")
            appendLine()
            appendLine("/** Every key of the string tables, grouped by prefix. Generated from en.json. */")
            appendLine("public object RinneStrings {")
            groups.forEach { (prefix, groupKeys) ->
                appendLine("    public object ${prefix.replaceFirstChar(Char::uppercaseChar)} {")
                groupKeys.sorted().forEach { key ->
                    val name = key.removePrefix(prefix)
                    appendLine("        public val $name: RinneStringKey = RinneStringKey(\"$key\")")
                }
                appendLine("    }")
            }
            appendLine("}")
        }
    }

    private fun prefixOf(key: String): String {
        val prefix = key.takeWhile { it.isLowerCase() || it.isDigit() }
        if (prefix.isEmpty() || prefix.length == key.length) {
            throw GradleException("Key \"$key\" must be <prefix><Name>, e.g. errorDefaultTitle")
        }
        return prefix
    }

    private fun signaturesSource(keys: Map<String, Any>): String = buildString {
        appendLine(HEADER)
        appendLine("package ${packageName.get()}")
        appendLine()
        appendLine("/** The placeholder signature of every key, from en.json. */")
        appendLine("internal object GeneratedSignatures {")
        appendLine("    val all: Map<String, String> = mapOf(")
        keys.toSortedMap().forEach { (key, value) ->
            val template = when (value) {
                is String -> value
                is Map<*, *> -> value["other"] as? String
                    ?: throw GradleException("Plural \"$key\" in en.json needs an \"other\" form")
                else -> throw GradleException("\"$key\" must be a string or an object of plural forms")
            }
            appendLine("        \"$key\" to ${kotlinString(template)},")
        }
        appendLine("    )")
        appendLine("}")
    }

    private fun bundledTablesSource(tables: Map<String, File>): String = buildString {
        appendLine(HEADER)
        appendLine("package ${packageName.get()}")
        appendLine()
        appendLine("/** The string tables built into every app: the base layer, offline and on first start. */")
        appendLine("internal object GeneratedBundledTables {")
        appendLine("    val all: Map<String, String> by lazy {")
        appendLine("        mapOf(")
        tables.forEach { (locale, file) ->
            // Split into chunks: one string constant on the JVM is limited to 64 KB.
            val chunks = file.readText().chunked(CHUNK_SIZE).joinToString(", ") { kotlinString(it) }
            appendLine("            \"$locale\" to listOf($chunks).joinToString(\"\"),")
        }
        appendLine("        )")
        appendLine("    }")
        appendLine("}")
    }

    private fun writeOsTexts(outputDir: File, locale: String, strings: Map<String, Any>) {
        val osTexts = strings.filterKeys { it.startsWith(OS_PREFIX) && it.length > OS_PREFIX.length }
            .mapValues { (key, value) -> value as? String ?: throw GradleException("OS text \"$key\" can't be a plural") }
        if (osTexts.isEmpty()) return

        val valuesDir = when (locale) {
            "en" -> "values"
            else -> "values-${locale.replace("-", "-r")}"
        }
        File(outputDir, "android/res/$valuesDir").mkdirs()
        File(outputDir, "android/res/$valuesDir/os_strings.xml").writeText(buildString {
            appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
            appendLine("<!-- Generated from rinne-shared/shared/strings/tables/$locale.json. Do not edit. -->")
            appendLine("<resources>")
            osTexts.forEach { (key, value) ->
                appendLine("    <string name=\"${androidName(key)}\">${androidEscape(value)}</string>")
            }
            appendLine("</resources>")
        })

        File(outputDir, "apple/$locale.lproj").mkdirs()
        File(outputDir, "apple/$locale.lproj/InfoPlist.strings").writeText(buildString {
            appendLine("/* Generated from rinne-shared/shared/strings/tables/$locale.json. Do not edit. */")
            osTexts.forEach { (key, value) ->
                appendLine("\"${appleInfoPlistKey(key)}\" = \"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\";")
            }
        })
    }

    /** `osAppName` → `app_name`. */
    private fun androidName(key: String): String = key.removePrefix(OS_PREFIX)
        .replace(Regex("([a-z0-9])([A-Z])"), "$1_$2")
        .lowercase()

    /** `osAppName` → `CFBundleDisplayName`; other OS texts keep their Info.plist key after `os`. */
    private fun appleInfoPlistKey(key: String): String = when (key) {
        "osAppName" -> "CFBundleDisplayName"
        else -> key.removePrefix(OS_PREFIX)
    }

    private fun androidEscape(value: String): String = value
        .replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
        .replace("'", "\\'").replace("\"", "\\\"")

    private fun kotlinString(value: String): String = buildString {
        append('"')
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '$' -> append("\\$")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
        append('"')
    }

    private companion object {
        const val HEADER = "// Generated from rinne-shared/shared/strings/tables. Do not edit."
        const val OS_PREFIX = "os"
        const val CHUNK_SIZE = 16_000
    }
}
