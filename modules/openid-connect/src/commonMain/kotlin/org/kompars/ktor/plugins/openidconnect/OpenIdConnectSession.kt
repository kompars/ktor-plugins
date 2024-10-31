package org.kompars.ktor.plugins.openidconnect

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
public data class Tokens(
    public val accessToken: String,
    public val refreshToken: String?,
    public val idToken: String,
    public val tokenType: String,
    public val expiresIn: Long,
)

@Serializable
public class UserInfo(public val json: JsonObject)

public val UserInfo.name: String get() = json["name"]?.jsonPrimitive?.content ?: error("Name not defined in user info")
public val UserInfo.email: String get() = json["email"]?.jsonPrimitive?.content ?: error("E-mail not defined in user info")

@Serializable
public class OpenIdConnectSession(
    public val tokens: Tokens,
    public val userInfo: UserInfo,
)
