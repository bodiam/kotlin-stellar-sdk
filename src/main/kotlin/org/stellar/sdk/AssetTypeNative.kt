package org.stellar.sdk

import org.stellar.sdk.xdr.AssetType

/**
 * Represents Stellar native asset - [lumens (XLM)](https://www.stellar.org/developers/learn/concepts/assets.html)
 * @see [Assets](https://www.stellar.org/developers/learn/concepts/assets.html)
 */
class AssetTypeNative : Asset() {

    override val type: String
        get() = "native"

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other!!.javaClass
    }

    override fun hashCode(): Int {
        return 0
    }

    override fun toXdr(): org.stellar.sdk.xdr.Asset {
        val xdr = org.stellar.sdk.xdr.Asset()
        xdr.discriminant = AssetType.ASSET_TYPE_NATIVE
        return xdr
    }
}
