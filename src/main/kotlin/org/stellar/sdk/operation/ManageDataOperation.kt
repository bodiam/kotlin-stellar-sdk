package org.stellar.sdk.operation

import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.DataValue
import org.stellar.sdk.xdr.ManageDataOp
import org.stellar.sdk.xdr.OperationType
import org.stellar.sdk.xdr.String64

/**
 * Represents [ManageData](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#manage-data) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class ManageDataOperation private constructor(val name: String, val value: ByteArray?) : Operation() {

    override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val op = ManageDataOp()
        val name = String64()
        name.string64 = this.name
        op.dataName = name

        if (value != null) {
            val dataValue = DataValue()
            dataValue.dataValue = this.value
            op.dataValue = dataValue
        }

        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.MANAGE_DATA
        body.manageDataOp = op

        return body
    }

    class Builder(private val name: String, val value: ByteArray?) {
        private var mSourceAccount: KeyPair? = null

        /**
         * Construct a new ManageOffer builder from a ManageDataOp XDR.
         * @param op [ManageDataOp]
         */
        internal constructor(op: ManageDataOp) : this(op.dataName.string64, op.dataValue?.dataValue)

        /**
         * Sets the source account for this operation.
         * @param sourceAccount The operation's source account.
         * @return Builder object so you can chain methods.
         */
        fun setSourceAccount(sourceAccount: KeyPair): Builder {
            this.mSourceAccount = sourceAccount
            return this
        }

        /**
         * Builds an operation
         */
        fun build(): ManageDataOperation {
            val operation = ManageDataOperation(name, value)
            if (mSourceAccount != null) {
                operation.sourceAccount = mSourceAccount
            }
            return operation
        }
    }
}
