package org.stellar.sdk

import com.google.common.base.Preconditions.checkArgument
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.io.BaseEncoding
import org.stellar.sdk.memo.Memo
import org.stellar.sdk.operation.Operation
import org.stellar.sdk.xdr.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

/**
 * Represents [Transaction](https://www.stellar.org/developers/learn/concepts/transactions.html) in Stellar network.
 */
class Transaction(val sourceAccount: KeyPair, val fee: Int, val sequenceNumber: Long, val operations: Array<Operation>,
                  val memo: Memo = Memo.none(), val timeBounds: TimeBounds?) {
    /**
     * Returns operations in this transaction.
     */
    private val mSignatures: MutableList<DecoratedSignature>

    val signatures: List<DecoratedSignature>
        get() = mSignatures

    init {
        checkArgument(operations.size > 0, "At least one operation required")
        mSignatures = ArrayList()
    }

    /**
     * Adds a new signature ed25519PublicKey to this transaction.
     * @param signer [KeyPair] object representing a signer
     */
    fun sign(signer: KeyPair) {
        val txHash = this.hash()
        mSignatures.add(signer.signDecorated(txHash))
    }

    /**
     * Adds a new sha256Hash signature to this transaction by revealing preimage.
     * @param preimage the sha256 hash of preimage should be equal to signer hash
     */
    fun sign(preimage: ByteArray) {
        val signature = Signature()
        signature.signature = preimage

        val hash = Util.hash(preimage)
        val signatureHintBytes = Arrays.copyOfRange(hash, hash.size - 4, hash.size)
        val signatureHint = SignatureHint()
        signatureHint.signatureHint = signatureHintBytes

        val decoratedSignature = DecoratedSignature()
        decoratedSignature.hint = signatureHint
        decoratedSignature.signature = signature

        mSignatures.add(decoratedSignature)
    }

    /**
     * Returns transaction hash.
     */
    fun hash(): ByteArray {
        return Util.hash(this.signatureBase()!!)
    }

    /**
     * Returns signature base.
     */
    fun signatureBase(): ByteArray? {
        if (Network.current() == null) {
            throw NoNetworkSelectedException()
        }

        try {
            val outputStream = ByteArrayOutputStream()
            // Hashed NetworkID
            outputStream.write(Network.current()!!.networkId)
            // Envelope Type - 4 bytes
            outputStream.write(ByteBuffer.allocate(4).putInt(EnvelopeType.ENVELOPE_TYPE_TX.value).array())
            // Transaction XDR bytes
            val txOutputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(txOutputStream)
            org.stellar.sdk.xdr.Transaction.encode(xdrOutputStream, this.toXdr())
            outputStream.write(txOutputStream.toByteArray())

            return outputStream.toByteArray()
        } catch (exception: IOException) {
            return null
        }

    }

    /**
     * Generates Transaction XDR object.
     */
    fun toXdr(): org.stellar.sdk.xdr.Transaction {
        // fee
        val fee = Uint32()
        fee.uint32 = this.fee
        // sequenceNumber
        val sequenceNumberUint = Int64()
        sequenceNumberUint.int64 = sequenceNumber
        val sequenceNumber = SequenceNumber()
        sequenceNumber.sequenceNumber = sequenceNumberUint
        // sourceAccount
        val sourceAccount = AccountID()
        sourceAccount.accountID = this.sourceAccount.xdrPublicKey
        // operations
        val operations = arrayOfNulls<org.stellar.sdk.xdr.Operation>(this.operations.size)
        for (i in this.operations.indices) {
            operations[i] = this.operations[i].toXdr()
        }
        // ext
        val ext = org.stellar.sdk.xdr.Transaction.TransactionExt()
        ext.discriminant = 0

        val transaction = org.stellar.sdk.xdr.Transaction()
        transaction.fee = fee
        transaction.seqNum = sequenceNumber
        transaction.sourceAccount = sourceAccount
        transaction.operations = operations
        transaction.memo = memo.toXdr()
        transaction.timeBounds = timeBounds?.toXdr()
        transaction.ext = ext
        return transaction
    }

    /**
     * Generates TransactionEnvelope XDR object. Transaction need to have at least one signature.
     */
    fun toEnvelopeXdr(): TransactionEnvelope {
        if (mSignatures.size == 0) {
            throw NotEnoughSignaturesException("Transaction must be signed by at least one signer. Use transaction.sign().")
        }

        val xdr = TransactionEnvelope()
        val transaction = this.toXdr()
        xdr.tx = transaction

        var signatures = mSignatures.toTypedArray()
        xdr.signatures = signatures
        return xdr
    }

    /**
     * Returns base64-encoded TransactionEnvelope XDR object. Transaction need to have at least one signature.
     */
    fun toEnvelopeXdrBase64(): String {
        try {
            val envelope = this.toEnvelopeXdr()
            val outputStream = ByteArrayOutputStream()
            val xdrOutputStream = XdrDataOutputStream(outputStream)
            TransactionEnvelope.encode(xdrOutputStream, envelope)

            val base64Encoding = BaseEncoding.base64()
            return base64Encoding.encode(outputStream.toByteArray())
        } catch (e: IOException) {
            throw AssertionError(e)
        }

    }

    /**
     * Builds a new Transaction object.
     */
    class Builder(private var mSourceAccount: TransactionBuilderAccount?) {
        private var mMemo: Memo = Memo.none()
        private var mTimeBounds: TimeBounds? = null
        internal var mOperations: MutableList<Operation>

        val operationsCount: Int
            get() = mOperations.size

        init {
            checkNotNull<TransactionBuilderAccount>(mSourceAccount, "sourceAccount cannot be null")
            mOperations = Collections.synchronizedList(ArrayList())
        }

        /**
         * Adds a new [operation](https://www.stellar.org/developers/learn/concepts/list-of-operations.html) to this transaction.
         * @param operation
         * @return Builder object so you can chain methods.
         * @see Operation
         */
        fun addOperation(operation: Operation): Builder {
            mOperations.add(operation)
            return this
        }

        /**
         * Adds a [memo](https://www.stellar.org/developers/learn/concepts/transactions.html) to this transaction.
         * @param memo
         * @return Builder object so you can chain methods.
         * @see Memo
         */
        fun addMemo(memo: Memo): Builder {
            mMemo = memo
            return this
        }

        /**
         * Adds a [time-bounds](https://www.stellar.org/developers/learn/concepts/transactions.html) to this transaction.
         * @param timeBounds
         * @return Builder object so you can chain methods.
         * @see TimeBounds
         */
        fun addTimeBounds(timeBounds: TimeBounds): Builder {
            if (mTimeBounds != null) {
                throw RuntimeException("TimeBounds has been already added.")
            }
            mTimeBounds = timeBounds
            return this
        }

        /**
         * Builds a transaction. It will increment sequence number of the source account.
         */
        fun build(): Transaction {
            var operations = mOperations.toTypedArray()
            val transaction = Transaction(mSourceAccount!!.keypair, operations.size * BASE_FEE, mSourceAccount!!.incrementedSequenceNumber, operations, mMemo, mTimeBounds)
            // Increment sequence number when there were no exceptions when creating a transaction
            this.mSourceAccount = mSourceAccount!!.withIncrementedSequenceNumber()
            return transaction
        }
    }

    companion object {
        private val BASE_FEE = 100

        /**
         * Creates a `Transaction` instance from previously build `TransactionEnvelope`
         * @param envelope Base-64 encoded `TransactionEnvelope`
         * @return
         * @throws IOException
         */
        @Throws(IOException::class)
        fun fromEnvelopeXdr(envelope: String): Transaction {
            val base64Encoding = BaseEncoding.base64()
            val bytes = base64Encoding.decode(envelope)

            val transactionEnvelope = TransactionEnvelope.decode(XdrDataInputStream(ByteArrayInputStream(bytes)))
            return fromEnvelopeXdr(transactionEnvelope)
        }

        /**
         * Creates a `Transaction` instance from previously build `TransactionEnvelope`
         * @param envelope
         * @return
         */
        fun fromEnvelopeXdr(envelope: TransactionEnvelope): Transaction {
            val tx = envelope.tx
            val mFee = tx.fee.uint32!!
            val mSourceAccount = KeyPair.fromXdrPublicKey(tx.sourceAccount.accountID)
            val mSequenceNumber = tx.seqNum.sequenceNumber.int64
            val mMemo = Memo.fromXdr(tx.memo)
            val mTimeBounds = TimeBounds.fromXdr(tx.timeBounds)

            val mOperations = arrayOf<Operation>()
            for (i in 0 until tx.operations.size) {
                mOperations[i] = Operation.fromXdr(tx.operations[i])
            }

            val transaction = Transaction(mSourceAccount, mFee, mSequenceNumber!!, mOperations, mMemo, mTimeBounds)

            for (signature in envelope.signatures) {
                transaction.mSignatures.add(signature)
            }

            return transaction
        }
    }
}
