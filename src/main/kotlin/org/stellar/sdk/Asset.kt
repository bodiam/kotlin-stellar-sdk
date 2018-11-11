package org.stellar.sdk

import org.stellar.sdk.xdr.AssetType

/**
 * Base Asset class.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
abstract class Asset internal constructor() {

    /**
     * Returns asset type. Possible types:
     *
     *  * `native`
     *  * `credit_alphanum4`
     *  * `credit_alphanum12`
     *
     */
    abstract val type: String

//    abstract override fun equals(`object`: Any?): Boolean

    /**
     * Generates XDR object from a given Asset object
     */
    abstract fun toXdr(): org.stellar.sdk.xdr.Asset

    companion object {

        @JvmStatic
        fun create(type: String, code: String, issuer: String): Asset {
            return if (type == "native") {
                AssetTypeNative()
            } else {
                Asset.createNonNativeAsset(code, KeyPair.fromAccountId(issuer))
            }
        }

        /**
         * Creates one of AssetTypeCreditAlphaNum4 or AssetTypeCreditAlphaNum12 object based on a `code` length
         * @param code Asset code
         * @param issuer Asset issuer
         */
        private fun createNonNativeAsset(code: String, issuer: KeyPair): Asset {
            return when {
                code.length in 1..4 -> AssetTypeCreditAlphaNum4(code, issuer)
                code.length in 5..12 -> AssetTypeCreditAlphaNum12(code, issuer)
                else -> throw AssetCodeLengthInvalidException()
            }
        }

        /**
         * Generates Asset object from a given XDR object
         * @param xdr XDR object
         */
        @JvmStatic
        fun fromXdr(xdr: org.stellar.sdk.xdr.Asset): Asset {
            return when (xdr.discriminant) {
                AssetType.ASSET_TYPE_NATIVE -> AssetTypeNative()
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> {
                    val assetCode4 = Util.paddedByteArrayToString(xdr.alphaNum4.assetCode)
                    val issuer4 = KeyPair.fromXdrPublicKey(xdr.alphaNum4.issuer.accountID)
                    AssetTypeCreditAlphaNum4(assetCode4, issuer4)
                }
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> {
                    val assetCode12 = Util.paddedByteArrayToString(xdr.alphaNum12.assetCode)
                    val issuer12 = KeyPair.fromXdrPublicKey(xdr.alphaNum12.issuer.accountID)
                    AssetTypeCreditAlphaNum12(assetCode12, issuer12)
                }
                else -> throw IllegalArgumentException("Unknown asset type ${xdr.discriminant}")
            }
        }
    }
}
