package org.kompars.ktor.plugins.openidconnect

import kotlin.io.encoding.*
import kotlinx.serialization.json.*

@OptIn(ExperimentalEncodingApi::class)
private val jwtBase64 = Base64.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

@OptIn(ExperimentalEncodingApi::class)
internal fun parseJwt(jwt: String): JsonObject {
    return jwt.split(".", limit = 3)[1]
        .let { jwtBase64.decode(it) }
        .decodeToString()
        .let { Json.parseToJsonElement(it) }
        .jsonObject
}
