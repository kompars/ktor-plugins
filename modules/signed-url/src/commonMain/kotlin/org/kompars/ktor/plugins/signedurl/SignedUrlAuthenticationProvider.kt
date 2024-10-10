package org.kompars.ktor.plugins.signedurl

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*

public data object SignaturePrincipal

public class SignedUrlAuthenticationProvider internal constructor(
    config: SignedUrlAuthenticationProviderConfig,
) : AuthenticationProvider(config) {
    private val authenticationFunction = config.authenticationFunction
    private val challengeFunction = config.challengeFunction
    private val signer = config.signer

    override suspend fun onAuthenticate(context: AuthenticationContext) {
        val call = context.call

        val principal = when (signer.verify(call.request.uri)) {
            true -> authenticationFunction(call, Url(call.request.uri))
            false -> null
        }

        if (principal != null) {
            context.principal(name, principal)
            return
        }

        val cause = when (signer.hasSignature(call.request.uri)) {
            true -> AuthenticationFailedCause.NoCredentials
            false -> AuthenticationFailedCause.InvalidCredentials
        }

        context.challenge("SignedUrlAuthentication", cause) { challenge, call ->
            challengeFunction(SignedUrlAuthenticationChallengeContext(call))

            if (!challenge.completed && call.response.status() != null) {
                challenge.complete()
            }
        }
    }
}

public class SignedUrlAuthenticationProviderConfig internal constructor(
    name: String?,
) : AuthenticationProvider.Config(name) {
    public lateinit var signer: UrlSigner

    internal var authenticationFunction: AuthenticationFunction<Url> = { SignaturePrincipal }
    internal var challengeFunction: suspend SignedUrlAuthenticationChallengeContext.() -> Unit = {}

    public fun validate(block: AuthenticationFunction<Url>) {
        authenticationFunction = block
    }

    public fun challenge(block: suspend SignedUrlAuthenticationChallengeContext.() -> Unit) {
        challengeFunction = block
    }
}

public class SignedUrlAuthenticationChallengeContext internal constructor(
    public val call: ApplicationCall,
)

public fun AuthenticationConfig.signedUrl(
    name: String? = null,
    configure: SignedUrlAuthenticationProviderConfig.() -> Unit = {},
) {
    val config = SignedUrlAuthenticationProviderConfig(name).apply(configure)
    val provider = SignedUrlAuthenticationProvider(config)

    register(provider)
}

public fun ApplicationCall.isSignedUrl(provider: String? = null): Boolean {
    return principal<SignaturePrincipal>(provider) != null
}
