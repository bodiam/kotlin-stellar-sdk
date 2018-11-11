package org.stellar.sdk.memo

import org.stellar.sdk.xdr.Memo
import org.stellar.sdk.xdr.MemoType

/**
 * Represents MEMO_RETURN.
 */
class MemoReturnHash : MemoHashAbstract {
    constructor(bytes: ByteArray) : super(bytes)

    constructor(hexString: String) : super(hexString)

    override fun toXdr(): Memo {
        val memo = Memo()
        memo.discriminant = MemoType.MEMO_RETURN

        val hash = org.stellar.sdk.xdr.Hash()
        hash.hash = bytes

        memo.retHash = hash
        return memo
    }
}
