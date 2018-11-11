@file:Suppress("FunctionName")

package org.stellar.sdk

import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.Asset.AssetAlphaNum12
import org.stellar.sdk.xdr.Asset.AssetAlphaNum4
import org.stellar.sdk.xdr.Int32
import org.stellar.sdk.xdr.PublicKey

fun Int32(value: Int): Int32 = Int32().apply { int32 = value }

fun AssetAlphaNum4(code: ByteArray, accountId: AccountID): AssetAlphaNum4 = AssetAlphaNum4().apply {
    assetCode = code
    issuer = accountId
}
fun AssetAlphaNum12(code: ByteArray, accountId: AccountID): AssetAlphaNum12 = AssetAlphaNum12().apply {
    assetCode = code
    issuer = accountId
}

fun AccountID(publicKey: PublicKey) : AccountID = AccountID().apply { accountID = publicKey }