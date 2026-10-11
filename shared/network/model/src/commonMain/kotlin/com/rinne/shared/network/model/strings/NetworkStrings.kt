package com.rinne.shared.network.model.strings

import kotlinx.serialization.Serializable

/**
 * `GET /api/v1/strings`: the languages the server has tables for. A table itself
 * (`GET /api/v1/strings/{locale}`) is the table file as is (`rinne-shared/shared/strings/tables`),
 * with an `ETag` that is a hash of its content; `If-None-Match` with it answers `304`.
 */
@Serializable
data class NetworkStringLocales(val locales: List<NetworkStringLocale>)

@Serializable
data class NetworkStringLocale(
    /** BCP 47 tag, e.g. `en`, `pt-BR`. */
    val locale: String,
    /** The language's own name for itself ("Русский"), from its table. */
    val name: String,
    /** The table's current ETag (without quotes). */
    val etag: String,
)
