package org.kompars.ktor.plugins.csp

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.util.*
import kotlin.io.encoding.*

@OptIn(ExperimentalEncodingApi::class)
public class ContentSecurityPolicySourceBuilder internal constructor(private val call: ApplicationCall) {
    internal val sources = mutableListOf<String>()

    public fun host(host: String) {
        sources += host
    }

    public fun host(url: Url) {
        sources += url.toString()
    }

    public fun originPath(path: String) {
        sources += url {
            protocol = URLProtocol.Companion.createOrDefault(call.request.origin.scheme)
            host = call.request.origin.serverHost
            port = call.request.origin.serverPort
            path(path)
        }
    }

    public fun scheme(scheme: String) {
        sources += "$scheme:"
    }

    public fun self() {
        sources += "'self'"
    }

    public fun unsafeEval() {
        sources += "'unsafe-eval'"
    }

    public fun wasmUnsafeEval() {
        sources += "'wasm-unsafe-eval'"
    }

    public fun unsafeHashes() {
        sources += "'unsafe-hashes'"
    }

    public fun unsafeInline() {
        sources += "'unsafe-inline'"
    }

    public fun none() {
        sources += "'none'"
    }

    public fun nonce(nonce: String) {
        sources += "'nonce-$nonce'"
    }

    public fun generatedNonce() {
        sources += "'nonce-${call.contentSecurityPolicyNonce}'"
    }

    public fun sha256(digest: String) {
        sources += "'sha256-$digest'"
    }

    public fun sha256(digest: ByteArray) {
        sources += "'sha256-${Base64.Default.encode(digest)}'"
    }

    public fun sha384(digest: String) {
        sources += "'sha384-$digest'"
    }

    public fun sha384(digest: ByteArray) {
        sources += "'sha384-${Base64.Default.encode(digest)}'"
    }

    public fun sha512(digest: String) {
        sources += "'sha512-$digest'"
    }

    public fun sha512(digest: ByteArray) {
        sources += "'sha512-${Base64.Default.encode(digest)}'"
    }

    public fun strictDynamic() {
        sources += "'strict-dynamic'"
    }

    public fun reportSample() {
        sources += "'report-sample'"
    }

    public fun inlineSpeculationRules() {
        sources += "'inline-speculation-rules'"
    }
}
