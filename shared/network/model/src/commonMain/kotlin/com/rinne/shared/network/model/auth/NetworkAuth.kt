package com.rinne.shared.network.model.auth

import com.rinne.libraries.date.time.core.RinneTimestamp
import kotlinx.serialization.Serializable

/** `POST /api/v1/auth/register` */
@Serializable
data class NetworkRegisterRequest(
    val email: String,
    val password: String,
)

/** `POST /api/v1/auth/login` */
@Serializable
data class NetworkLoginRequest(
    val email: String,
    val password: String,
)

/** `POST /api/v1/auth/refresh` and `POST /api/v1/auth/logout` */
@Serializable
data class NetworkRefreshTokenRequest(
    val refreshToken: String,
)

/**
 * Issued by register, login and refresh. Send [accessToken] as `Authorization: Bearer <token>`;
 * exchange [refreshToken] for a new pair once the access token expires. Every refresh rotates the
 * refresh token, so the old one stops working.
 */
@Serializable
data class NetworkAuthSession(
    val user: NetworkUser,
    val accessToken: String,
    val accessTokenExpiresAt: RinneTimestamp,
    val refreshToken: String,
    val refreshTokenExpiresAt: RinneTimestamp,
)

/** `GET /api/v1/auth/me` */
@Serializable
data class NetworkUser(
    val id: String,
    val email: String,
    val createdAt: RinneTimestamp,
)
