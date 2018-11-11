package org.stellar.sdk.operation

import org.stellar.sdk.KeyPair
import org.stellar.sdk.Util
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.AllowTrustOp
import org.stellar.sdk.xdr.AssetType
import org.stellar.sdk.xdr.OperationType

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [AllowTrust](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#allow-trust) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class AllowTrustOperation private constructor(trustor: KeyPair, assetCode: String,
                                              /**
                                               * Flag indicating whether the trustline is authorized.
                                               */
                                              val authorize: Boolean) : Operation() {

    /**
     * The account of the recipient of the trustline.
     */
    val trustor: KeyPair
    /**
     * The asset of the trustline the source account is authorizing. For example, if a gateway wants to allow another account to hold its USD credit, the type is USD.
     */
    val assetCode: String

    init {
        this.trustor = checkNotNull(trustor, "trustor cannot be null")
        this.assetCode = checkNotNull(assetCode, "assetCode cannot be null")
    }

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = AllowTrustOp()

        // trustor
        val trustor = AccountID()
        trustor.accountID = this.trustor.xdrPublicKey
        op.trustor = trustor
        // asset
        val asset = AllowTrustOp.AllowTrustOpAsset()
        if (assetCode.length <= 4) {
            asset.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM4
            asset.assetCode4 = Util.paddedByteArray(assetCode, 4)
        } else {
            asset.discriminant = AssetType.ASSET_TYPE_CREDIT_ALPHANUM12
            asset.assetCode12 = Util.paddedByteArray(assetCode, 12)
        }
        op.asset = asset
        // authorize
        op.authorize = authorize

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.ALLOW_TRUST
        body.allowTrustOp = op
        return body
    }

    /**
     * Builds AllowTrust operation.
     * @see AllowTrustOperation
     */
    class Builder {
        private val trustor: KeyPair
        private val assetCode: String
        private val authorize: Boolean

        private var mSourceAccount: KeyPair? = null

        internal constructor(op: AllowTrustOp) {
            trustor = KeyPair.fromXdrPublicKey(op.trustor.accountID)
            when (op.asset.discriminant) {
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM4 -> assetCode = String(op.asset.assetCode4).trim { it <= ' ' }
                AssetType.ASSET_TYPE_CREDIT_ALPHANUM12 -> assetCode = String(op.asset.assetCode12).trim { it <= ' ' }
                else -> throw RuntimeException("Unknown asset code")
            }
            authorize = op.authorize!!
        }

        /**
         * Creates a new AllowTrust builder.
         * @param trustor The account of the recipient of the trustline.
         * @param assetCode The asset of the trustline the source account is authorizing. For example, if a gateway wants to allow another account to hold its USD credit, the type is USD.
         * @param authorize Flag indicating whether the trustline is authorized.
         */
        constructor(trustor: KeyPair, assetCode: String, authorize: Boolean) {
            this.trustor = trustor
            this.assetCode = assetCode
            this.authorize = authorize
        }

        /**
         * Set source account of this operation
         * @param sourceAccount Source account
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            mSourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): AllowTrustOperation {
            val operation = AllowTrustOperation(trustor, assetCode, authorize)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
