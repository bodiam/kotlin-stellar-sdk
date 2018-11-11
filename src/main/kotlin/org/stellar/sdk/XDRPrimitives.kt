@file:Suppress("FunctionName")

package org.stellar.sdk

import org.stellar.sdk.xdr.*
import org.stellar.sdk.xdr.Asset.AssetAlphaNum12
import org.stellar.sdk.xdr.Asset.AssetAlphaNum4

fun Int32(value: Int): Int32 = Int32().apply { int32 = value }
fun Uint256(value: ByteArray): Uint256 = Uint256().apply { uint256 = value }

fun AssetAlphaNum4(code: ByteArray, accountId: AccountID): AssetAlphaNum4 = AssetAlphaNum4().apply {
    assetCode = code
    issuer = accountId
}
fun AssetAlphaNum12(code: ByteArray, accountId: AccountID): AssetAlphaNum12 = AssetAlphaNum12().apply {
    assetCode = code
    issuer = accountId
}

fun AccountID(publicKey: PublicKey) : AccountID = AccountID().apply { accountID = publicKey }

fun SignerKey(signerKeyType: SignerKeyType, value: Uint256): SignerKey = SignerKey().apply {
    discriminant = signerKeyType
    preAuthTx = value
}