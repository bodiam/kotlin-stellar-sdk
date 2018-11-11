package org.stellar.sdk.operation

import org.stellar.sdk.Asset
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Price
import org.stellar.sdk.xdr.CreatePassiveOfferOp
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType

import java.math.BigDecimal

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [CreatePassiveOffer](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#create-passive-offer) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class CreatePassiveOfferOperation private constructor(selling: Asset, buying: Asset, amount: String, price: String) : Operation() {
    /**
     * The asset being sold in this operation
     */
    val selling: Asset
    /**
     * The asset being bought in this operation
     */
    val buying: Asset
    /**
     * Amount of selling being sold.
     */
    val amount: String
    /**
     * Price of 1 unit of selling in terms of buying.
     */
    val price: String

    init {
        this.selling = checkNotNull(selling, "selling cannot be null")
        this.buying = checkNotNull(buying, "buying cannot be null")
        this.amount = checkNotNull(amount, "amount cannot be null")
        this.price = checkNotNull(price, "price cannot be null")
    }

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = CreatePassiveOfferOp()
        op.selling = selling.toXdr()
        op.buying = buying.toXdr()
        val amount = Int64()
        amount.int64 = Operation.toXdrAmount(this.amount)
        op.amount = amount
        val price = Price.fromString(this.price)
        op.price = price.toXdr()

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.CREATE_PASSIVE_OFFER
        body.createPassiveOfferOp = op

        return body
    }

    /**
     * Builds CreatePassiveOffer operation.
     * @see CreatePassiveOfferOperation
     */
    class Builder {

        private val selling: Asset
        private val buying: Asset
        private val amount: String
        private val price: String

        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreatePassiveOffer builder from a CreatePassiveOfferOp XDR.
         * @param op
         */
        internal constructor(op: CreatePassiveOfferOp) {
            selling = Asset.fromXdr(op.selling)
            buying = Asset.fromXdr(op.buying)
            amount = Operation.fromXdrAmount(op.amount.int64!!.toLong())
            val n = op.price.n.int32!!.toInt()
            val d = op.price.d.int32!!.toInt()
            price = BigDecimal(n).divide(BigDecimal(d)).toString()
        }

        /**
         * Creates a new CreatePassiveOffer builder.
         * @param selling The asset being sold in this operation
         * @param buying The asset being bought in this operation
         * @param amount Amount of selling being sold.
         * @param price Price of 1 unit of selling in terms of buying.
         * @throws ArithmeticException when amount has more than 7 decimal places.
         */
        constructor(selling: Asset, buying: Asset, amount: String, price: String) {
            this.selling = checkNotNull(selling, "selling cannot be null")
            this.buying = checkNotNull(buying, "buying cannot be null")
            this.amount = checkNotNull(amount, "amount cannot be null")
            this.price = checkNotNull(price, "price cannot be null")
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
        fun build(): CreatePassiveOfferOperation {
            val operation = CreatePassiveOfferOperation(selling, buying, amount, price)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
