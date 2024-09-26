package org.kompars.ktor.plugins.csp

import io.ktor.server.application.*
import io.ktor.util.*

internal val ContentSecurityPolicyNonce = AttributeKey<String>("ContentSecurityPolicyNonce")

public val ApplicationCall.contentSecurityPolicyNonce: String get() = attributes[ContentSecurityPolicyNonce]
public val ApplicationCall.cspNonce: String get() = contentSecurityPolicyNonce
