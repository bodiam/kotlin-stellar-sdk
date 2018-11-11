package org.stellar.sdk.operation

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.io.BaseEncoding
import org.stellar.sdk.KeyPair
import org.stellar.sdk.xdr.AccountID
import org.stellar.sdk.xdr.OperationType.*
import org.stellar.sdk.xdr.XdrDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.math.BigDecimal

/**
 * Abstract class for operations.
 */
abstract class Operation internal constructor() {

    /**
     * Sets operation source account.
     * @param keypair
     */
    // TODO: apparently, cannot be set to null, but when creating a new operation, it's null
    var sourceAccount: KeyPair? = null
        internal set(keypair) {
            field = checkNotNull(keypair, "keypair cannot be null")
        }

    /**
     * Generates Operation XDR object.
     */
    fun toXdr(): org.stellar.sdk.xdr.Operation {
        val xdr = org.stellar.sdk.xdr.Operation()
        if (sourceAccount != null) {
            val sourceAccount = AccountID()
            sourceAccount.accountID = this.sourceAccount!!.xdrPublicKey
            xdr.sourceAccount = sourceAccount
        }
        xdr.body = toOperationBody()
        return xdr
    }

    /**
     * Returns base64-encoded Operation XDR object.
     */
    fun toXdrBase64(): String {
        try {
            val operation = this.toXdr()
            val outputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(outputStream)
            org.stellar.sdk.xdr.Operation.encode(xdrOutputStream, operation)
            val base64Encoding = BaseEncoding.base64()
            return base64Encoding.encode(outputStream.toByteArray())
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    /**
     * Generates OperationBody XDR object
     * @return OperationBody XDR object
     */
    internal abstract fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody

    companion object {

        private val ONE = BigDecimal(10).pow(7)

        fun toXdrAmount(value: String): Long {
            val amount = BigDecimal(value).multiply(Operation.ONE)
            return amount.longValueExact()
        }

        fun fromXdrAmount(value: Long): String {
            val amount = BigDecimal(value).divide(Operation.ONE)
            return amount.toPlainString()
        }

        /**
         * Returns new Operation object from Operation XDR object.
         * @param xdr XDR object
         */
        fun fromXdr(xdr: org.stellar.sdk.xdr.Operation): Operation {
            val body = xdr.body
            val operation: Operation = when (body.discriminant) {
                CREATE_ACCOUNT -> CreateAccountOperation.Builder(body.createAccountOp).build()
                PAYMENT -> PaymentOperation.Builder(body.paymentOp).build()
                PATH_PAYMENT -> PathPaymentOperation.Builder(body.pathPaymentOp).build()
                MANAGE_OFFER -> ManageOfferOperation.Builder(body.manageOfferOp).build()
                CREATE_PASSIVE_OFFER -> CreatePassiveOfferOperation.Builder(body.createPassiveOfferOp).build()
                SET_OPTIONS -> SetOptionsOperation.Builder(body.setOptionsOp).build()
                CHANGE_TRUST -> ChangeTrustOperation.Builder(body.changeTrustOp).build()
                ALLOW_TRUST -> AllowTrustOperation.Builder(body.allowTrustOp).build()
                ACCOUNT_MERGE -> AccountMergeOperation.Builder(body).build()
                MANAGE_DATA -> ManageDataOperation.Builder(body.manageDataOp).build()
                BUMP_SEQUENCE -> BumpSequenceOperation.Builder(body.bumpSequenceOp).build()
                else -> throw RuntimeException("Unknown operation body " + body.discriminant)
            }
            if (xdr.sourceAccount != null) {
                operation.sourceAccount = KeyPair.fromXdrPublicKey(xdr.sourceAccount.accountID)
            }
            return operation
        }
    }
}
