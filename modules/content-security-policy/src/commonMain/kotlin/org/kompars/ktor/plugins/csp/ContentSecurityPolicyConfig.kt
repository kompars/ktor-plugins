package org.kompars.ktor.plugins.csp

import io.ktor.server.application.*

public class ContentSecurityPolicyConfig internal constructor() {
    internal val sourceBuilders = mutableMapOf<String, ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit>()

    public fun defaultSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["default-src"] = block
    }

    public fun childSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["child-src"] = block
    }

    public fun connectSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["connect-src"] = block
    }

    public fun fontSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["font-src"] = block
    }

    public fun frameSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["frame-src"] = block
    }

    public fun imgSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["img-src"] = block
    }

    public fun manifestSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["manifest-src"] = block
    }

    public fun mediaSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["media-src"] = block
    }

    public fun objectSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["object-src"] = block
    }

    public fun prefetchSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["prefetch-src"] = block
    }

    public fun scriptSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["script-src"] = block
    }

    public fun scriptSrcElem(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["script-src-elem"] = block
    }

    public fun scriptSrcAttr(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["script-src-attr"] = block
    }

    public fun styleSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["style-src"] = block
    }

    public fun styleSrcElem(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["style-src-elem"] = block
    }

    public fun styleSrcAttr(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["style-src-attr"] = block
    }

    public fun workerSrc(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["worker-src"] = block
    }

    public fun baseUri(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["base-uri"] = block
    }

    public fun formAction(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["form-action"] = block
    }

    public fun frameAncestors(block: ContentSecurityPolicySourceBuilder.(ApplicationCall) -> Unit) {
        sourceBuilders["frame-ancestors"] = block
    }
}
