package org.stellar.sdk

/**
 * Specifies interface for Account object used in [org.stellar.sdk.Transaction.Builder]
 */
interface TransactionBuilderAccount {
    /**
     * Returns keypair associated with this Account
     */
    val keypair: KeyPair

    /**
     * Returns current sequence number ot this Account.
     */
    val sequenceNumber: Long

    /**
     * Returns sequence number incremented by one, but does not increment internal counter.
     */
    val incrementedSequenceNumber: Long

    /**
     * Increments sequence number in this object by one.
     */
    @BreakingChange
    fun withIncrementedSequenceNumber() : Account
}
