package com.rinne.shared.network.model.error

import kotlinx.serialization.Serializable

/**
 * Body of every non-2xx API response.
 *
 * @property code stable, machine-readable error identifier (`notes.note_not_found`,
 * `auth.invalid_credentials`, `validation_failed`, ...). Clients branch on it, never on [message].
 * @property message human-readable description for logs and debugging; not localized.
 * @property violations field-level problems, non-empty only for `validation_failed`.
 */
@Serializable
data class NetworkApiError(
    val code: String,
    val message: String,
    val violations: List<NetworkFieldViolation> = emptyList(),
)

/**
 * A single rejected field of a request body.
 *
 * @property field the JSON property name, e.g. `title`.
 * @property code machine-readable reason, e.g. `blank`, `too_long`, `invalid_format`.
 */
@Serializable
data class NetworkFieldViolation(
    val field: String,
    val code: String,
    val message: String,
)
