package org.stellar.sdk.memo

import org.stellar.sdk.xdr.MemoType

/**
 * Represents MEMO_NONE.
 */
class MemoNone : Memo() {
    override fun toXdr(): org.stellar.sdk.xdr.Memo {
        val memo = org.stellar.sdk.xdr.Memo()
        memo.discriminant = MemoType.MEMO_NONE
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return !(other == null || javaClass != other.javaClass)
    }
}
