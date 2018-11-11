package org.stellar.sdk

import org.stellar.sdk.xdr.SignerKey
import org.stellar.sdk.xdr.SignerKeyType
import org.stellar.sdk.xdr.SignerKeyType.SIGNER_KEY_TYPE_PRE_AUTH_TX
import org.stellar.sdk.xdr.Uint256

/**
 * Signer is a helper class that creates [SignerKey] objects.
 */
object Signer {
    /**
     * Create `ed25519PublicKey` [SignerKey] from
     * a [KeyPair]
     * @param keyPair
     * @return org.stellar.sdk.xdr.SignerKey
     */
    fun ed25519PublicKey(keyPair: KeyPair): SignerKey {
        return keyPair.xdrSignerKey
    }

    /**
     * Create `sha256Hash` [SignerKey] from
     * a sha256 hash of a preimage.
     * @param hash
     * @return org.stellar.sdk.xdr.SignerKey
     */
    fun sha256Hash(hash: ByteArray): SignerKey {
        val signerKey = SignerKey()
        val value = Signer.createUint256(hash)

        signerKey.discriminant = SignerKeyType.SIGNER_KEY_TYPE_HASH_X
        signerKey.hashX = value

        return signerKey
    }

    /**
     * Create `preAuthTx` [SignerKey] from
     * a [org.stellar.sdk.xdr.Transaction] hash.
     * @param tx
     * @return org.stellar.sdk.xdr.SignerKey
     */
    fun preAuthTx(tx: Transaction): SignerKey {
        return SignerKey(SIGNER_KEY_TYPE_PRE_AUTH_TX, Signer.createUint256(tx.hash()))
    }

    /**
     * Create `preAuthTx` [SignerKey] from
     * a transaction hash.
     * @param hash
     * @return org.stellar.sdk.xdr.SignerKey
     */
    fun preAuthTx(hash: ByteArray): SignerKey {
        return SignerKey(SIGNER_KEY_TYPE_PRE_AUTH_TX, Signer.createUint256(hash))
    }

    private fun createUint256(hash: ByteArray): Uint256 {
        if (hash.size != 32) {
            throw RuntimeException("hash must be 32 bytes long")
        }
        return Uint256(hash)
    }
}
