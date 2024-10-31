package org.kompars.ktor.plugins.openidconnect

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

public class OpenIdConnectAuthenticationProvider internal constructor(
    config: OpenIdConnectAuthenticationProviderConfig,
) : AuthenticationProvider(config) {
    private val authenticationFunction = config.authenticationFunction
    private val challengeFunction = config.challengeFunction

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call
        val session = call.getOpenIdConnectSession()
        val principal = session?.let { authenticationFunction(call, session) }

        if (principal != null) {
            context.principal(name, principal)
            return
        }

        val cause = when {
            session == null -> AuthenticationFailedCause.NoCredentials
            else -> AuthenticationFailedCause.InvalidCredentials
        }

        context.challenge("OpenIdConnectAuthentication", cause) { challenge, call ->
            challengeFunction(OpenIdConnectAuthenticationChallengeContext(call))

            if (!call.isHandled) {
                call.respondRedirect {
                    path("/auth/login")
                    parameters["originalUrl"] = call.request.uri
                }
            }

            if (!challenge.completed && call.response.status() != null) {
                challenge.complete()
            }
        }
    }
}

public class OpenIdConnectAuthenticationProviderConfig internal constructor(
    name: String?,
) : AuthenticationProvider.Config(name) {
    internal var authenticationFunction: AuthenticationFunction<OpenIdConnectSession> = { it }
    internal var challengeFunction: suspend OpenIdConnectAuthenticationChallengeContext.() -> Unit = {}

    public fun validate(block: AuthenticationFunction<OpenIdConnectSession>) {
        authenticationFunction = block
    }

    public fun challenge(block: suspend OpenIdConnectAuthenticationChallengeContext.() -> Unit) {
        challengeFunction = block
    }
}

public class OpenIdConnectAuthenticationChallengeContext internal constructor(
    public val call: ApplicationCall,
)

public fun AuthenticationConfig.openIdConnect(
    name: String? = null,
    configure: OpenIdConnectAuthenticationProviderConfig.() -> Unit = {},
) {
    val config = OpenIdConnectAuthenticationProviderConfig(name).apply(configure)
    val provider = OpenIdConnectAuthenticationProvider(config)

    register(provider)
}
