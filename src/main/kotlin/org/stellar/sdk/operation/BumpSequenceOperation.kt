package org.stellar.sdk.operation

import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.BumpSequenceOp
import org.stellar.sdk.xdr.Int64
import org.stellar.sdk.xdr.OperationType
import org.stellar.sdk.xdr.SequenceNumber

import com.google.common.base.Preconditions.checkNotNull

class BumpSequenceOperation private constructor(val bumpTo: Long) : Operation() {

    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = BumpSequenceOp()
        val bumpTo = Int64()
        bumpTo.int64 = this.bumpTo
        val sequenceNumber = SequenceNumber()
        sequenceNumber.sequenceNumber = bumpTo
        op.bumpTo = sequenceNumber

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.BUMP_SEQUENCE
        body.bumpSequenceOp = op

        return body
    }

    class Builder {
        private val bumpTo: Long

        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new BumpSequence builder from a BumpSequence XDR.
         * @param op [BumpSequenceOp]
         */
        internal constructor(op: BumpSequenceOp) {
            bumpTo = op.bumpTo.sequenceNumber.int64!!
        }

        /**
         * Creates a new BumpSequence builder.
         * @param bumpTo Sequence number to bump to
         */
        constructor(bumpTo: Long) {
            this.bumpTo = bumpTo
        }

        /**
         * Sets the source account for this operation.
         * @param sourceAccount The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): BumpSequenceOperation.Builder {
            mSourceAccount = checkNotNull(sourceAccount, "sourceAccount cannot be null")
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): BumpSequenceOperation {
            val operation = BumpSequenceOperation(bumpTo)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
