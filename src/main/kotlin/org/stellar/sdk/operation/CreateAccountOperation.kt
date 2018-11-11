package org.stellar.sdk.operation

import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.CreateAccountOp
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [CreateAccount](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#create-account) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class CreateAccountOperation private constructor(destination: KeyPair, startingBalance: String) : Operation() {

    /**
     * Account that is created and funded
     */
    val destination: KeyPair
    /**
     * Amount of XLM to send to the newly created account.
     */
    val startingBalance: String

    init {
        this.destination = checkNotNull(destination, "destination cannot be null")
        this.startingBalance = checkNotNull(startingBalance, "startingBalance cannot be null")
    }

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = CreateAccountOp()
        val destination = AccountID()
        destination.accountID = this.destination.xdrPublicKey
        op.destination = destination
        val startingBalance = Int64()
        startingBalance.int64 = Operation.toXdrAmount(this.startingBalance)
        op.startingBalance = startingBalance

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.CREATE_ACCOUNT
        body.createAccountOp = op
        return body
    }

    /**
     * Builds CreateAccount operation.
     * @see CreateAccountOperation
     */
    class Builder {
        private val destination: KeyPair
        private val startingBalance: String

        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new CreateAccount builder from a CreateAccountOp XDR.
         * @param op [CreateAccountOp]
         */
        internal constructor(op: CreateAccountOp) {
            destination = KeyPair.fromXdrPublicKey(op.destination.accountID)
            startingBalance = Operation.fromXdrAmount(op.startingBalance.int64!!.toLong())
        }

        /**
         * Creates a new CreateAccount builder.
         * @param destination The destination keypair (uses only the public key).
         * @param startingBalance The initial balance to start with in lumens.
         * @throws ArithmeticException when startingBalance has more than 7 decimal places.
         */
        constructor(destination: KeyPair, startingBalance: String) {
            this.destination = destination
            this.startingBalance = startingBalance
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
        fun build(): CreateAccountOperation {
            val operation = CreateAccountOperation(destination, startingBalance)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
