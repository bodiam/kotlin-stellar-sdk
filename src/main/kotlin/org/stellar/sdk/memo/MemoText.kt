package org.stellar.sdk.memo

import com.google.common.base.Objects
import org.stellar.sdk.xdr.MemoType

import java.nio.charset.Charset

/**
 * Represents MEMO_TEXT.
 */
class MemoText(val text: String) : Memo() {

    init {
        val length = text.toByteArray(Charset.forName("UTF-8")).size
        if (length > 28) {
            throw MemoTooLongException("text must be <= 28 bytes. length=" + length.toString())
        }
    }

    override fun toXdr(): org.stellar.sdk.xdr.Memo {
        val memo = org.stellar.sdk.xdr.Memo()
        memo.discriminant = MemoType.MEMO_TEXT
        memo.text = text
        return memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val memoText = other as MemoText?
        return Objects.equal(text, memoText!!.text)
    }
}
