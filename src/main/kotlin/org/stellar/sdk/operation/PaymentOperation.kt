package org.stellar.sdk.operation

import org.stellar.sdk.Asset
import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType
import org.stellar.sdk.xdr.PaymentOp

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [Payment](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#payment) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class PaymentOperation private constructor(destination: KeyPair, asset: Asset, amount: String) : Operation() {

    /**
     * Account that receives the payment.
     */
    val destination: KeyPair
    /**
     * Asset to send to the destination account.
     */
    val asset: Asset
    /**
     * Amount of the asset to send.
     */
    val amount: String

    init {
        this.destination = checkNotNull(destination, "destination cannot be null")
        this.asset = checkNotNull(asset, "asset cannot be null")
        this.amount = checkNotNull(amount, "amount cannot be null")
    }

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = PaymentOp()

        // destination
        val destination = AccountID()
        destination.accountID = this.destination.xdrPublicKey
        op.destination = destination
        // asset
        op.asset = asset.toXdr()
        // amount
        val amount = Int64()
        amount.int64 = Operation.toXdrAmount(this.amount)
        op.amount = amount

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.PAYMENT
        body.paymentOp = op
        return body
    }

    /**
     * Builds Payment operation.
     * @see PathPaymentOperation
     */
    class Builder {
        private val destination: KeyPair
        private val asset: Asset
        private val amount: String

        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new PaymentOperation builder from a PaymentOp XDR.
         * @param op [PaymentOp]
         */
        internal constructor(op: PaymentOp) {
            destination = KeyPair.fromXdrPublicKey(op.destination.accountID)
            asset = Asset.fromXdr(op.asset)
            amount = Operation.fromXdrAmount(op.amount.int64!!.toLong())
        }

        /**
         * Creates a new PaymentOperation builder.
         * @param destination The destination keypair (uses only the public key).
         * @param asset The asset to send.
         * @param amount The amount to send in lumens.
         * @throws ArithmeticException when amount has more than 7 decimal places.
         */
        constructor(destination: KeyPair, asset: Asset, amount: String) {
            this.destination = destination
            this.asset = asset
            this.amount = amount
        }

        /**
         * Sets the source account for this operation.
         * @param account The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(account: KeyPair): Builder {
            mSourceAccount = account
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): PaymentOperation {
            val operation = PaymentOperation(destination, asset, amount)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
