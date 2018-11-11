package org.stellar.sdk.memo

import org.stellar.sdk.xdr.MemoType

/**
 * Represents MEMO_HASH.
 */
class MemoHash : MemoHashAbstract {
    constructor(bytes: ByteArray) : super(bytes) {}

    constructor(hexString: String) : super(hexString) {}

    override fun toXdr(): org.stellar.sdk.xdr.Memo {
        val memo = org.stellar.sdk.xdr.Memo()
        memo.discriminant = MemoType.MEMO_HASH

        val hash = org.stellar.sdk.xdr.Hash()
        hash.hash = bytes

        memo.hash = hash
        return memo
    }
}
