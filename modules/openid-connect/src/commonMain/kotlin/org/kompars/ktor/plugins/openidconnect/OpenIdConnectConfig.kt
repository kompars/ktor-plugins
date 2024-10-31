package org.kompars.ktor.plugins.openidconnect

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

public class OpenIdConnectConfig internal constructor() {
    public lateinit var issuer: String
    public lateinit var clientId: String
    public lateinit var clientSecret: String
    public var scopes: Set<String> = emptySet()

    public var httpClient: HttpClient = HttpClient()
    public val session: OpenIdConnectSessionConfig = OpenIdConnectSessionConfig()

    public var path: String = "/auth"
    public var postLoginUrl: String = "/"
    public var postLogoutUrl: String = "/"

    internal var onLogin: suspend ApplicationCall.(OpenIdConnectSession) -> Unit = {}

    public fun onLogin(block: suspend ApplicationCall.(OpenIdConnectSession) -> Unit) {
        onLogin = block
    }

    public fun session(block: OpenIdConnectSessionConfig.() -> Unit) {
        session.apply(block)
    }
}

public class OpenIdConnectSessionConfig internal constructor() {
    public var storage: SessionStorage = SessionStorageMemory()
    public var cookieName: String = "SessionId"

    internal val provider: OpenIdConnectSessionProvider by lazy { OpenIdConnectSessionProvider(storage) }
}

public class OpenIdConnectSessionProvider internal constructor(private val storage: SessionStorage) {
    public suspend fun get(sessionId: String): OpenIdConnectSession? {
        return try {
            Json.decodeFromString<OpenIdConnectSession>(storage.read(sessionId))
        } catch (e: NoSuchElementException) {
            return null
        }
    }

    public suspend fun set(sessionId: String, session: OpenIdConnectSession) {
        storage.write(sessionId, Json.encodeToString(session))
    }
}

internal val OpenIdConnectConfigAttribute = AttributeKey<OpenIdConnectConfig>("OpenIdConnectConfig")

internal suspend fun ApplicationCall.getOpenIdConnectSession(): OpenIdConnectSession? {
    val config = application.attributes[OpenIdConnectConfigAttribute]
    return request.cookies[config.session.cookieName]?.let { config.session.provider.get(it) }
}
