package com.rinne.libraries.network.client.offline.mutation

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private const val TEMP_ID_PREFIX = "tmp_"
private val TempIdRegex = Regex("${TEMP_ID_PREFIX}[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")

@OptIn(ExperimentalUuidApi::class)
internal fun randomId(): String = Uuid.random().toString()

internal fun generateTempId(): String = TEMP_ID_PREFIX + randomId()

fun String.isTempId(): Boolean = TempIdRegex.matches(this)

internal fun String.findTempIds(): Set<String> = TempIdRegex.findAll(this).map { it.value }.toSet()

internal fun String.containsTempId(): Boolean = TempIdRegex.containsMatchIn(this)

/** Temporary ids are unique UUIDs, so a textual replacement can't hit anything else. */
internal fun String.replaceTempIds(mappings: Map<String, String>): String =
    if (mappings.isEmpty()) this else TempIdRegex.replace(this) { mappings[it.value] ?: it.value }
