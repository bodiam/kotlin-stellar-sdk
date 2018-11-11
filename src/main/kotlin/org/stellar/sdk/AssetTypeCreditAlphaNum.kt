package org.stellar.sdk

import java.util.Arrays

/**
 * Base class for AssetTypeCreditAlphaNum4 and AssetTypeCreditAlphaNum12 subclasses.
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
abstract class AssetTypeCreditAlphaNum(val code: String, issuer: KeyPair) : Asset() {
    protected val mIssuer: KeyPair = KeyPair.fromAccountId(issuer.accountId)

    /**
     * Returns asset issuer
     */
    val issuer: KeyPair
        get() = KeyPair.fromAccountId(mIssuer.accountId)

    override fun hashCode(): Int {
        return Arrays.hashCode(arrayOf<Any>(this.code, this.issuer.accountId))
    }

    override fun equals(other: Any?): Boolean {
        if (this.javaClass != other!!.javaClass) {
            return false
        }

        val o = other as AssetTypeCreditAlphaNum?

        return this.code == o!!.code && this.issuer.accountId == o.issuer.accountId
    }
}
