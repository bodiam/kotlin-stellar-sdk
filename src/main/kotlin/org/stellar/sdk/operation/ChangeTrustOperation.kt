package org.stellar.sdk.operation

import org.stellar.sdk.Asset
import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.ChangeTrustOp
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [ChangeTrust](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#change-trust) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ChangeTrustOperation private constructor(asset: Asset, limit: String) : Operation() {

    /**
     * The asset of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the line is USD.
     */
    val asset: Asset
    /**
     * The limit of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the limit is 200.
     */
    val limit: String

    init {
        this.asset = checkNotNull(asset, "asset cannot be null")
        this.limit = checkNotNull(limit, "limit cannot be null")
    }

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = ChangeTrustOp()
        op.line = asset.toXdr()
        val limit = Int64()
        limit.int64 = Operation.toXdrAmount(this.limit)
        op.limit = limit

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.CHANGE_TRUST
        body.changeTrustOp = op
        return body
    }

    /**
     * Builds ChangeTrust operation.
     * @see ChangeTrustOperation
     */
    class Builder {
        private val asset: Asset
        private val limit: String

        private var mSourceAccount: KeyPair? = null

        internal constructor(op: ChangeTrustOp) {
            asset = Asset.fromXdr(op.line)
            limit = Operation.fromXdrAmount(op.limit.int64!!.toLong())
        }

        /**
         * Creates a new ChangeTrust builder.
         * @param asset The asset of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the line is USD.
         * @param limit The limit of the trustline. For example, if a gateway extends a trustline of up to 200 USD to a user, the limit is 200.
         * @throws ArithmeticException when limit has more than 7 decimal places.
         */
        constructor(asset: Asset, limit: String) {
            this.asset = checkNotNull(asset, "asset cannot be null")
            this.limit = checkNotNull(limit, "limit cannot be null")
        }

        /**
         * Set source account of this operation
         * @param sourceAccount Source account
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            mSourceAccount = checkNotNull(sourceAccount, "sourceAccount cannot be null")
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): ChangeTrustOperation {
            val operation = ChangeTrustOperation(asset, limit)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
