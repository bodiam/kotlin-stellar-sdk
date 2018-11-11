package org.stellar.sdk.operation

import org.stellar.sdk.Asset
import org.stellar.sdk.KeyPair
import org.stellar.sdk.Price
import org.stellar.sdk.xdr.*

import java.math.BigDecimal

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [ManageOffer](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#manage-offer) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ManageOfferOperation private constructor(selling: Asset, buying: Asset, amount: String, price: String,
                                               /**
                                                * The ID of the offer.
                                                */
                                               val offerId: Long) : Operation() {

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
    }// offerId can be null

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = ManageOfferOp()
        op.selling = selling.toXdr()
        op.buying = buying.toXdr()
        val amount = Int64()
        amount.int64 = Operation.toXdrAmount(this.amount)
        op.amount = amount
        val price = Price.fromString(this.price)
        op.price = price.toXdr()
        val offerId = Uint64()
        offerId.uint64 = java.lang.Long.valueOf(this.offerId)
        op.offerID = offerId

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.MANAGE_OFFER
        body.manageOfferOp = op

        return body
    }

    /**
     * Builds ManageOffer operation. If you want to update existing offer use
     * [org.stellar.sdk.ManageOfferOperation.Builder.setOfferId].
     * @see ManageOfferOperation
     */
    class Builder {

        private val selling: Asset
        private val buying: Asset
        private val amount: String
        private val price: String
        private var offerId: Long = 0

        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreateAccount builder from a CreateAccountOp XDR.
         * @param op [CreateAccountOp]
         */
        internal constructor(op: ManageOfferOp) {
            selling = Asset.fromXdr(op.selling)
            buying = Asset.fromXdr(op.buying)
            amount = Operation.fromXdrAmount(op.amount.int64!!.toLong())
            val n = op.price.n.int32!!.toInt()
            val d = op.price.d.int32!!.toInt()
            price = BigDecimal(n).divide(BigDecimal(d)).toString()
            offerId = op.offerID.uint64!!.toLong()
        }

        /**
         * Creates a new ManageOffer builder. If you want to update existing offer use
         * [org.stellar.sdk.ManageOfferOperation.Builder.setOfferId].
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
         * Sets offer ID. `0` creates a new offer. Set to existing offer ID to change it.
         * @param offerId
         */
        fun setOfferId(offerId: Long): Builder {
            this.offerId = offerId
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
        fun build(): ManageOfferOperation {
            val operation = ManageOfferOperation(selling, buying, amount, price, offerId)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
