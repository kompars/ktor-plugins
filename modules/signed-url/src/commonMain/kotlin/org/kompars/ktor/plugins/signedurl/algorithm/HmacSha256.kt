package org.kompars.ktor.plugins.signedurl.algorithm

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.*
import org.kompars.ktor.plugins.signedurl.*

public class HmacSha256(key: ByteArray) : SignatureAlgorithm {
    private val hmacKey = CryptographyProvider.Default
        .get(HMAC)
        .keyDecoder(SHA256)
        .decodeFromByteArrayBlocking(HMAC.Key.Format.RAW, key)

    private val signatureGenerator = hmacKey.signatureGenerator()
    private val signatureVerifier = hmacKey.signatureVerifier()

    override fun sign(data: ByteArray): ByteArray {
        return signatureGenerator.generateSignatureBlocking(data)
    }

    override fun verify(data: ByteArray, signature: ByteArray): Boolean {
        return signatureVerifier.tryVerifySignatureBlocking(data, signature)
    }
}
