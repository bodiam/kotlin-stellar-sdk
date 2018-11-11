package org.stellar.sdk

import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.AssetType

/**
 * Represents all assets with codes 5-12 characters long.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
class AssetTypeCreditAlphaNum12(code: String, issuer: KeyPair) : AssetTypeCreditAlphaNum(code, issuer) {

    override val type: String
        get() = "credit_alphanum12"

    init {
        if (code.length < 5 || code.length > 12) {
            throw AssetCodeLengthInvalidException("Asset's code '$code' should have length between 5 & 12 inclusive")
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = org.stellar.sdk.xdr.Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
        val credit = org.stellar.sdk.xdr.Asset.AssetAlphaNum12()
        credit.assetCode = Util.paddedByteArray(code, 12)
        val accountID = AccountID()
        accountID.accountID = issuer.xdrPublicKey
        credit.issuer = accountID
        xdr.alphaNum12 = credit
        return xdr
    }
}
