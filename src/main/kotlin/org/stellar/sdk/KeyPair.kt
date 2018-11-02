package org.stellar.sdk

import net.i2p.crypto.eddsa.EdDSAEngine
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import net.i2p.crypto.eddsa.KeyPairGenerator
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec
import org.stellar.sdk.xdr.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.security.SignatureException
import java.util.*

/**
 * Holds a Stellar keypair.
 */
class KeyPair
/**
 * Creates a new KeyPair from the given public and private keys.
 * @param publicKey
 * @param privateKey
 */
@JvmOverloads constructor(private val publicKey: EdDSAPublicKey, private val mPrivateKey: EdDSAPrivateKey? = null) {

    /**
     * Returns the human readable account ID encoded in strkey.
     */
    val accountId: String
        get() = StrKey.encodeStellarAccountId(publicKey.abyte)

    /**
     * Returns the human readable secret seed encoded in strkey.
     */
    val secretSeed: CharArray?
        get() = if(mPrivateKey != null) StrKey.encodeStellarSecretSeed(mPrivateKey.seed) else null


    val signatureHint: SignatureHint
        get() {
            try {
                val publicKeyBytesStream = ByteArrayOutputStream()
                val xdrOutputStream = XdrDataOutputStream(publicKeyBytesStream)
                PublicKey.encode(xdrOutputStream, xdrPublicKey)
                val publicKeyBytes = publicKeyBytesStream.toByteArray()
                val signatureHintBytes = Arrays.copyOfRange(publicKeyBytes, publicKeyBytes.size - 4, publicKeyBytes.size)

                return SignatureHint().apply {
                    signatureHint = signatureHintBytes
                }
            } catch (e: IOException) {
                throw AssertionError(e)
            }
        }

    val xdrPublicKey: PublicKey
        get() = PublicKey().apply {
            discriminant = PublicKeyType.PUBLIC_KEY_TYPE_ED25519
            ed25519 = Uint256().apply {
                uint256 = publicKey.abyte
            }
        }

    val xdrSignerKey: SignerKey
        get() = SignerKey().apply {
            discriminant = SignerKeyType.SIGNER_KEY_TYPE_ED25519
            ed25519 = Uint256().apply {
                uint256 = publicKey.abyte
            }
        }

    /**
     * Returns true if this Keypair is capable of signing
     */
    fun canSign(): Boolean {
        return mPrivateKey != null
    }

    /**
     * Sign the provided data with the keypair's private key.
     * @param data The data to sign.
     * @return signed bytes, null if the private key for this keypair is null.
     */
    fun sign(data: ByteArray): ByteArray {
        if (mPrivateKey == null) {
            throw RuntimeException("KeyPair does not contain secret key. Use KeyPair.fromSecretSeed method to create a new KeyPair with a secret key.")
        }
        try {
            val sgr = EdDSAEngine(MessageDigest.getInstance("SHA-512"))
            sgr.initSign(mPrivateKey)
            sgr.update(data)
            return sgr.sign()
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }

    }

    /**
     * Sign the provided data with the keypair's private key and returns [DecoratedSignature].
     * @param data
     */
    fun signDecorated(data: ByteArray): DecoratedSignature {
        val signatureBytes = this.sign(data)

        val signature = org.stellar.sdk.xdr.Signature()
        signature.signature = signatureBytes

        val decoratedSignature = DecoratedSignature()
        decoratedSignature.hint = this.signatureHint
        decoratedSignature.signature = signature
        return decoratedSignature
    }

    /**
     * Verify the provided data and signature match this keypair's public key.
     * @param data The data that was signed.
     * @param signature The signature.
     * @return True if they match, false otherwise.
     * @throws RuntimeException
     */
    fun verify(data: ByteArray, signature: ByteArray): Boolean {
        try {
            val sgr = EdDSAEngine(MessageDigest.getInstance("SHA-512"))
            sgr.initVerify(publicKey)
            sgr.update(data)
            return sgr.verify(signature)
        } catch (e: SignatureException) {
            return false
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }

    }

    companion object {

        private val ed25519 = EdDSANamedCurveTable.ED_25519_CURVE_SPEC

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         * @param seed Char array containing strkey encoded Stellar secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: CharArray): KeyPair {
            val decoded = StrKey.decodeStellarSecretSeed(seed)
            val keypair = fromSecretSeed(decoded)
            Arrays.fill(decoded, 0.toByte())
            return keypair
        }

        /**
         * **Insecure** Creates a new Stellar KeyPair from a strkey encoded Stellar secret seed.
         * This method is <u>insecure</u>. Use only if you are aware of security implications.
         * @see [Using Password-Based Encryption](http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html.PBEEx)
         *
         * @param seed The strkey encoded Stellar secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: String): KeyPair {
            val charSeed = seed.toCharArray()
            val decoded = StrKey.decodeStellarSecretSeed(charSeed)
            val keypair = fromSecretSeed(decoded)
            Arrays.fill(charSeed, ' ')
            return keypair
        }

        /**
         * Creates a new Stellar keypair from a raw 32 byte secret seed.
         * @param seed The 32 byte secret seed.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromSecretSeed(seed: ByteArray): KeyPair {
            val privKeySpec = EdDSAPrivateKeySpec(seed, ed25519)
            val publicKeySpec = EdDSAPublicKeySpec(privKeySpec.a.toByteArray(), ed25519)
            return KeyPair(EdDSAPublicKey(publicKeySpec), EdDSAPrivateKey(privKeySpec))
        }

        /**
         * Creates a new Stellar KeyPair from a strkey encoded Stellar account ID.
         * @param accountId The strkey encoded Stellar account ID.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromAccountId(accountId: String): KeyPair {
            val decoded = StrKey.decodeStellarAccountId(accountId)
            return fromPublicKey(decoded)
        }

        /**
         * Creates a new Stellar keypair from a 32 byte address.
         * @param publicKey The 32 byte public key.
         * @return [KeyPair]
         */
        @JvmStatic
        fun fromPublicKey(publicKey: ByteArray): KeyPair {
            val publicKeySpec = EdDSAPublicKeySpec(publicKey, ed25519)
            return KeyPair(EdDSAPublicKey(publicKeySpec))
        }

        /**
         * Generates a random Stellar keypair.
         * @return a random Stellar keypair.
         */
        @JvmStatic
        fun random(): KeyPair {
            val keypair = KeyPairGenerator().generateKeyPair()
            return KeyPair(keypair.public as EdDSAPublicKey, keypair.private as EdDSAPrivateKey)
        }

        @JvmStatic
        fun fromXdrPublicKey(key: PublicKey): KeyPair {
            return KeyPair.fromPublicKey(key.ed25519.uint256)
        }

        @JvmStatic
        fun fromXdrSignerKey(key: SignerKey): KeyPair {
            return KeyPair.fromPublicKey(key.ed25519.uint256)
        }
    }
}
