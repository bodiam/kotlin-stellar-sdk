package org.stellar.sdk.operation

import org.stellar.sdk.Asset
import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType
import org.stellar.sdk.xdr.PathPaymentOp

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [PathPayment](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#path-payment) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class PathPaymentOperation private constructor(val sendAsset: Asset, val sendMax: String, val destination: KeyPair,
                                               val destAsset: Asset, val destAmount: String, path: Array<Asset>?) : Operation() {

    /**
     * The assets (other than send asset and destination asset) involved in the offers the path takes. For example, if you can only find a path from USD to EUR through XLM and BTC, the path would be USD - XLM - BTC - EUR and the path would contain XLM and BTC.
     */
    val path: Array<Asset>

    init {
        if (path == null) {
            this.path = emptyArray()
        } else {
            checkArgument(path.size <= 5, "The maximum number of assets in the path is 5")
            this.path = path
        }
    }

    override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = PathPaymentOp()

        // sendAsset
        op.sendAsset = sendAsset.toXdr()
        // sendMax
        val sendMax = Int64()
        sendMax.int64 = Operation.toXdrAmount(this.sendMax)
        op.sendMax = sendMax
        // destination
        val destination = AccountID()
        destination.accountID = this.destination.xdrPublicKey
        op.destination = destination
        // destAsset
        op.destAsset = destAsset.toXdr()
        // destAmount
        val destAmount = Int64()
        destAmount.int64 = Operation.toXdrAmount(this.destAmount)
        op.destAmount = destAmount
        // path
        val path = arrayOfNulls<org.stellar.sdk.xdr.Asset>(this.path.size)
        for (i in this.path.indices) {
            path[i] = this.path[i].toXdr()
        }
        op.path = path

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.PATH_PAYMENT
        body.pathPaymentOp = op
        return body
    }

    /**
     * Builds PathPayment operation.
     * @see PathPaymentOperation
     */
    class Builder {
        private val sendAsset: Asset
        private val sendMax: String
        private val destination: KeyPair
        private val destAsset: Asset
        private val destAmount: String
        private var path: MutableList<Asset> = arrayListOf()

        private var mSourceAccount: KeyPair? = null

        internal constructor(op: PathPaymentOp) {
            sendAsset = Asset.fromXdr(op.sendAsset)
            sendMax = Operation.fromXdrAmount(op.sendMax.int64!!.toLong())
            destination = KeyPair.fromXdrPublicKey(op.destination.accountID)
            destAsset = Asset.fromXdr(op.destAsset)
            destAmount = Operation.fromXdrAmount(op.destAmount.int64!!.toLong())
            for (i in 0 until op.path.size) {
                path.add(Asset.fromXdr(op.path[i]))
            }
        }

        /**
         * Creates a new PathPaymentOperation builder.
         * @param sendAsset The asset deducted from the sender's account.
         * @param sendMax The asset deducted from the sender's account.
         * @param destination Payment destination
         * @param destAsset The asset the destination account receives.
         * @param destAmount The amount of destination asset the destination account receives.
         * @throws ArithmeticException when sendMax or destAmount has more than 7 decimal places.
         */
        constructor(sendAsset: Asset, sendMax: String, destination: KeyPair,
                    destAsset: Asset, destAmount: String) {
            this.sendAsset = checkNotNull(sendAsset, "sendAsset cannot be null")
            this.sendMax = checkNotNull(sendMax, "sendMax cannot be null")
            this.destination = checkNotNull(destination, "destination cannot be null")
            this.destAsset = checkNotNull(destAsset, "destAsset cannot be null")
            this.destAmount = checkNotNull(destAmount, "destAmount cannot be null")
        }

        /**
         * Sets path for this operation
         * @param path The assets (other than send asset and destination asset) involved in the offers the path takes. For example, if you can only find a path from USD to EUR through XLM and BTC, the path would be USD - XLM - BTC - EUR and the path field would contain XLM and BTC.
         * @return Builder object so you can chain methods.
         */
        fun setPath(path: Array<Asset>): Builder {
            checkArgument(path.size <= 5, "The maximum number of assets in the path is 5")
            this.path = path.toMutableList()
            return this
        }

        /**
         * Sets the source account for this operation.
         * @param sourceAccount The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            mSourceAccount = checkNotNull(sourceAccount, "sourceAccount cannot be null")
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): PathPaymentOperation {
            val operation = PathPaymentOperation(sendAsset, sendMax, destination, destAsset, destAmount, path.toTypedArray())
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
