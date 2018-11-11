package org.stellar.sdk

import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.AssetType

/**
 * Represents all assets with codes 1-4 characters long.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
class AssetTypeCreditAlphaNum4(code: String, issuer: KeyPair) : AssetTypeCreditAlphaNum(code, issuer) {

    override val type: String
        get() = "credit_alphanum4"

    init {
        if (code.isEmpty() || code.length > 4) {
            throw AssetCodeLengthInvalidException("Asset's code '$code' should have length no greater than 4")
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = org.stellar.sdk.xdr.Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
        val credit = org.stellar.sdk.xdr.Asset.AssetAlphaNum4()
        credit.assetCode = Util.paddedByteArray(code, 4)
        val accountID = AccountID()
        accountID.accountID = issuer.xdrPublicKey
        credit.issuer = accountID
        xdr.alphaNum4 = credit
        return xdr
    }
}
