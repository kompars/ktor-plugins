package org.kompars.ktor.plugins.openidconnect

import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.util.*
import kotlinx.serialization.json.*

internal const val OAUTH_PROVIDER = "openid-connect-oauth-provider"

public val OpenIdConnect: ApplicationPlugin<OpenIdConnectConfig> = createApplicationPlugin("OpenIdConnect", ::OpenIdConnectConfig) {
    val config = pluginConfig
    val redirectUrls = mutableMapOf<String, String>()

    application.attributes.put(OpenIdConnectConfigAttribute, config)

    application.authentication {
        oauth(OAUTH_PROVIDER) {
            client = config.httpClient
            urlProvider = {
                url {
                    encodedPath = "${config.path}/callback"
                    parameters.clear()
                }
            }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = OAUTH_PROVIDER,
                    authorizeUrl = config.authorizationUrl,
                    accessTokenUrl = config.tokenUrl,
                    clientId = config.clientId,
                    clientSecret = config.clientSecret,
                    defaultScopes = listOf("openid") + config.scopes,
                    requestMethod = HttpMethod.Post,
                    onStateCreated = { call, state ->
                        call.parameters["originalUrl"]?.let { redirectUrls[state] = it }
                    }
                )
            }
        }
    }

    application.routing {
        route(config.path) {
            authenticate(OAUTH_PROVIDER) {
                get("/login") {}

                get("/callback") {
                    val tokenResponse = call.principal<OAuthAccessTokenResponse.OAuth2>(OAUTH_PROVIDER)!!

                    val tokens = Tokens(
                        accessToken = tokenResponse.accessToken,
                        refreshToken = tokenResponse.refreshToken,
                        idToken = tokenResponse.extraParameters["id_token"]!!,
                        tokenType = tokenResponse.tokenType,
                        expiresIn = tokenResponse.expiresIn,
                    )

                    val userInfoResponse = config.httpClient.get(config.userInfoUrl) {
                        expectSuccess = true
                        headers.append("Authorization", "Bearer ${tokenResponse.accessToken}")
                    }

                    val userInfo = userInfoResponse.bodyAsText()
                        .let { Json.parseToJsonElement(it) }
                        .let { UserInfo(it.jsonObject) }

                    val session = OpenIdConnectSession(tokens, userInfo)
                    val sessionId = parseJwt(tokens.idToken)["sid"]?.jsonPrimitive?.content ?: generateNonce()

                    config.onLogin(call, session)
                    config.session.provider.set(sessionId, session)

                    call.response.cookies.append(
                        name = config.session.cookieName,
                        value = sessionId,
                        secure = config.session.cookieSecure,
                        httpOnly = true,
                        path = "/",
                        extensions = mapOf("SameSite" to "Lax"),
                    )

                    call.respondRedirect(redirectUrls[tokenResponse.state] ?: config.postLoginUrl)
                }
            }

            get("/end-session") {
                when (val session = call.getOpenIdConnectSession()) {
                    null -> call.respondRedirect(config.postLogoutUrl)
                    else -> call.respondRedirect {
                        takeFrom(config.endSessionUrl)
                        parameters["id_token_hint"] = session.tokens.idToken
                        parameters["post_logout_redirect_uri"] = call.url {
                            path("${config.path}/logout")
                        }
                    }
                }
            }

            get("/logout") {
                call.request.cookies[config.session.cookieName]?.let { sessionId ->
                    config.session.storage.invalidate(sessionId)
                }

                call.respondRedirect(config.postLogoutUrl)
            }

            post("/back-channel-logout") {
                val logoutToken = parseJwt(call.receiveParameters()["logout_token"]!!)
                val sessionId = logoutToken["sid"]!!.jsonPrimitive.content

                config.session.storage.invalidate(sessionId)

                call.respondText("OK")
            }
        }
    }
}
