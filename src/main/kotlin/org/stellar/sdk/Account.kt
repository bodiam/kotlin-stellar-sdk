package org.stellar.sdk

/**
 * Represents an account in Stellar network with it's sequence number.
 * Account object is required to build a [Transaction].
 * @see org.stellar.sdk.Transaction.Builder
 */
data class Account(override val keypair: KeyPair, override val sequenceNumber: Long) : TransactionBuilderAccount {

    override val incrementedSequenceNumber: Long
        get() = sequenceNumber + 1

    /**
     * Increments sequence number in this object by one.
     */
    override fun withIncrementedSequenceNumber() = this.copy(sequenceNumber = sequenceNumber + 1)
}
