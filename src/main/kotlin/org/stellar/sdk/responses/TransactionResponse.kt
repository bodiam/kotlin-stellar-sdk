package org.stellar.sdk.responses

import com.google.gson.annotations.SerializedName
import org.stellar.sdk.KeyPair
import org.stellar.sdk.memo.Memo

import com.google.common.base.Preconditions.checkNotNull

/**
 * Represents transaction response.
 * @see [Transaction documentation](https://www.stellar.org/developers/horizon/reference/resources/transaction.html)
 *
 * @see org.stellar.sdk.requests.TransactionsRequestBuilder
 *
 * @see org.stellar.sdk.Server.transactions
 */
class TransactionResponse(@field:SerializedName("hash") val hash: String,
                          @field:SerializedName("ledger") val ledger: Long?,
                          @field:SerializedName("created_at") val createdAt: String,
                          @field:SerializedName("source_account") val sourceAccount: KeyPair,
                          @field:SerializedName("paging_token") val pagingToken: String,
                          @field:SerializedName("source_account_sequence") val sourceAccountSequence: Long?,
                          @field:SerializedName("fee_paid") val feePaid: Long?,
                          @field:SerializedName("operation_count") val operationCount: Int?,
                          @field:SerializedName("envelope_xdr") val envelopeXdr: String,
                          @field:SerializedName("result_xdr") val resultXdr: String,
                          @field:SerializedName("result_meta_xdr") val resultMetaXdr: String,
                        // GSON won't serialize `transient` variables automatically. We need this behaviour
                        // because Memo is an abstract class and GSON tries to instantiate it.
                          @field:Transient var memo: Memo?,
                          @field:SerializedName("_links") val links: Links) : Response() {


    /**
     * Links connected to transaction.
     */
    class Links internal constructor(@field:SerializedName("account")
                                     val account: Link, @field:SerializedName("effects")
                                     val effects: Link, @field:SerializedName("ledger")
                                     val ledger: Link, @field:SerializedName("operations")
                                     val operations: Link, @field:SerializedName("self")
                                     val self: Link, @field:SerializedName("precedes")
                                     val precedes: Link, @field:SerializedName("succeeds")
                                     val succeeds: Link)
}
