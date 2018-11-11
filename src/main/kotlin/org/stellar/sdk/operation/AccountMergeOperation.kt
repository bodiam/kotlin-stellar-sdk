package org.stellar.sdk.operation

import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.Operation.OperationBody
import org.stellar.sdk.xdr.OperationType

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents [AccountMerge](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#account-merge) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class AccountMergeOperation private constructor(destination: KeyPair) : Operation() {

    /**
     * The account that receives the remaining XLM balance of the source account.
     */
    val destination: KeyPair

    init {
        this.destination = checkNotNull(destination, "destination cannot be null")
    }

    internal override fun toOperationBody(): OperationBody {
        val body = OperationBody()
        val destination = AccountID()
        destination.accountID = this.destination.xdrPublicKey
        body.destination = destination
        body.discriminant = OperationType.ACCOUNT_MERGE
        return body
    }

    /**
     * Builds AccountMerge operation.
     * @see AccountMergeOperation
     */
    class Builder {
        private val destination: KeyPair

        private var mSourceAccount: KeyPair? = null

        internal constructor(op: OperationBody) {
            destination = KeyPair.fromXdrPublicKey(op.destination.accountID)
        }

        /**
         * Creates a new AccountMerge builder.
         * @param destination The account that receives the remaining XLM balance of the source account.
         */
        constructor(destination: KeyPair) {
            this.destination = destination
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
        fun build(): AccountMergeOperation {
            val operation = AccountMergeOperation(destination)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
