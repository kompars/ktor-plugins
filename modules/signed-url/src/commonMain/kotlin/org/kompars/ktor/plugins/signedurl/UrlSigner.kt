package org.kompars.ktor.plugins.signedurl

import io.ktor.http.*
import kotlin.time.*
import kotlinx.datetime.*

public interface SignatureAlgorithm {
    public fun sign(data: ByteArray): ByteArray
    public fun verify(data: ByteArray, signature: ByteArray): Boolean
}

@OptIn(ExperimentalStdlibApi::class)
public class UrlSigner(
    private val algorithm: SignatureAlgorithm,
    private val signatureParameter: String = "signature",
    private val expiresParameter: String = "expires",
) {
    public fun sign(url: String, expiration: Duration): String {
        return sign(url, Clock.System.now() + expiration)
    }

    public fun sign(url: String, expires: Instant? = null): String {
        val builder = URLBuilder().takeFrom(url)

        if (expires != null) {
            builder.parameters[expiresParameter] = expires.toEpochMilliseconds().toString()
        }

        val data = builder.clone()
            .build()
            .encodedPathAndQuery
            .encodeToByteArray()

        val signature = algorithm.sign(data).toHexString()

        builder.parameters[signatureParameter] = signature

        return when (builder.host.isEmpty()) {
            true -> builder.build().encodedPathAndQuery
            false -> builder.buildString()
        }
    }

    public fun hasSignature(url: String): Boolean {
        return Url(url).parameters.contains(signatureParameter)
    }

    public fun verify(url: String, now: Instant = Clock.System.now()): Boolean {
        val builder = URLBuilder().takeFrom(url)

        val signature = try {
            builder.parameters[signatureParameter]?.hexToByteArray() ?: return false
        } catch (e: Exception) {
            return false
        }

        builder.parameters.remove(signatureParameter)

        val data = builder.clone().build()
            .encodedPathAndQuery
            .encodeToByteArray()

        val expires = builder.parameters[expiresParameter]
            ?.toLongOrNull()
            ?.let { Instant.fromEpochMilliseconds(it) }

        return algorithm.verify(data, signature) && (expires == null || expires >= now)
    }
}
