package org.stellar.sdk

import org.stellar.sdk.xdr.AssetType
import org.stellar.sdk.xdr.AssetType.*
import org.stellar.sdk.xdr.Asset as XDRAsset

/**
 * Base Asset class.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
abstract class Asset {

    /**
     * Generates XDR object from a given Asset object
     */
    abstract fun toXdr(): XDRAsset

    companion object {

        @JvmStatic
        fun create(type: String, code: String, issuer: String): Asset {
            return if (type == "native") {
                NativeAsset
            } else {
                Asset.createNonNativeAsset(code, KeyPair.fromAccountId(issuer))
            }
        }

        /**
         * Creates one of IssuedAsset4 or IssuedAsset12 object based on a `code` length
         * @param code Asset code
         * @param issuer Asset issuer
         */
        private fun createNonNativeAsset(code: String, issuer: KeyPair): Asset {
            return when {
                code.length in 1..4 -> IssuedAsset4(code, issuer)
                code.length in 5..12 -> IssuedAsset12(code, issuer)
                else -> throw AssetCodeLengthInvalidException()
            }
        }

        /**
         * Generates Asset object from a given XDR object
         * @param xdr XDR object
         */
        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
        @JvmStatic
        fun fromXdr(xdr: org.stellar.sdk.xdr.Asset): Asset {
            return when (xdr.discriminant) {
                ASSET_TYPE_NATIVE -> NativeAsset
                ASSET_TYPE_CREDIT_ALPHANUM4 -> {
                    val code = Util.paddedByteArrayToString(xdr.alphaNum4.assetCode)
                    val issuer = KeyPair.fromXdrPublicKey(xdr.alphaNum4.issuer.accountID)
                    IssuedAsset4(code, issuer)
                }
                ASSET_TYPE_CREDIT_ALPHANUM12 -> {
                    val code = Util.paddedByteArrayToString(xdr.alphaNum12.assetCode)
                    val issuer = KeyPair.fromXdrPublicKey(xdr.alphaNum12.issuer.accountID)
                    IssuedAsset12(code, issuer)
                }
            }
        }
    }
}

/**
 * Represents Stellar native asset - [lumens (XLM)](https://www.stellar.org/developers/learn/concepts/assets.html)
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
object NativeAsset : Asset() {

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = org.stellar.sdk.xdr.Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_NATIVE
        return xdr
    }
}


/**
 * Base class for IssuedAsset4 and IssuedAsset12 subclasses.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
abstract class NonNativeAsset(open val code: String, open val issuer: KeyPair, val type: String) : Asset()


/**
 * Represents all assets with codes 5-12 characters long.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
data class IssuedAsset12(override val code: String, override val issuer: KeyPair) : NonNativeAsset(code, issuer, "credit_alphanum12") {

    init {
        if (code.length < 5 || code.length > 12) {
            throw AssetCodeLengthInvalidException("Asset's code '$code' should have length between 5 & 12 inclusive")
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = XDRAsset()
        val accountID = AccountID(issuer.xdrPublicKey)
        val credit = AssetAlphaNum12(Util.paddedByteArray(code, 12), accountID)
        xdr.alphaNum12 = credit
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
        return xdr
    }
}


/**
 * Represents all assets with codes 1-4 characters long.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
data class IssuedAsset4(override val code: String, override val issuer: KeyPair) : NonNativeAsset(code, issuer, "credit_alphanum4") {

    init {
        if (code.isEmpty() || code.length > 4) {
            throw AssetCodeLengthInvalidException("Asset's code '$code' should have length no greater than 4")
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = XDRAsset()
        val accountID = AccountID(issuer.xdrPublicKey)
        val credit = AssetAlphaNum4(Util.paddedByteArray(code, 4), accountID)
        xdr.alphaNum4 = credit
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
        return xdr
    }
}

