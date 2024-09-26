package org.kompars.ktor.plugins.csp

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.server.response.*
import io.ktor.util.*

public val ContentSecurityPolicy: RouteScopedPlugin<ContentSecurityPolicyConfig> =
    createRouteScopedPlugin("ContentSecurityPolicy", ::ContentSecurityPolicyConfig) {
        on(CallSetup) { call ->
            call.attributes.put(ContentSecurityPolicyNonce, generateNonce())
        }

        onCall { call ->
            val contentSecurityPolicy = pluginConfig.sourceBuilders
                .mapValues { (_, builder) -> ContentSecurityPolicySourceBuilder(call).apply { builder(call) }.sources }
                .filterValues { it.isNotEmpty() }
                .map { (directive, sources) -> "$directive " + sources.joinToString(" ") }
                .joinToString("; ")

            call.response.headers.appendIfAbsent("Content-Security-Policy", contentSecurityPolicy)
        }
    }

public val CSP: RouteScopedPlugin<ContentSecurityPolicyConfig> = ContentSecurityPolicy
