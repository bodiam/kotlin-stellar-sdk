package org.stellar.sdk.memo

import org.stellar.sdk.xdr.MemoType
import org.stellar.sdk.xdr.Uint64

/**
 * Represents MEMO_ID.
 */
class MemoId(val id: Long) : Memo() {

    init {
        if (java.lang.Long.compareUnsigned(id, 0) < 0) {
            throw IllegalArgumentException("id must be a positive number")
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Memo {
        val memo = org.stellar.sdk.xdr.Memo()
        memo.discriminant = MemoType.MEMO_ID
        val idXdr = Uint64()
        idXdr.uint64 = id
        memo.id = idXdr
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val memoId = other as MemoId?
        return id == memoId!!.id
    }
}
