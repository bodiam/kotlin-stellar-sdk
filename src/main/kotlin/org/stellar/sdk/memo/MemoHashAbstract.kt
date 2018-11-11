package org.stellar.sdk.memo

import com.google.common.base.Objects
import com.google.common.io.BaseEncoding
import org.stellar.sdk.Util

abstract class MemoHashAbstract(var bytes: ByteArray) : Memo() {

    /**
     *
     * Returns hex representation of bytes contained in this memo.
     *
     *
     * Example:
     * `
     * MemoHash memo = new MemoHash("4142434445");
     * memo.getHexValue(); // 4142434445000000000000000000000000000000000000000000000000000000
     * memo.getTrimmedHexValue(); // 4142434445
    ` *
     */
    val hexValue: String
        get() = BaseEncoding.base16().lowerCase().encode(this.bytes)

    /**
     *
     * Returns hex representation of bytes contained in this memo until null byte (0x00) is found.
     *
     *
     * Example:
     * `
     * MemoHash memo = new MemoHash("4142434445");
     * memo.getHexValue(); // 4142434445000000000000000000000000000000000000000000000000000000
     * memo.getTrimmedHexValue(); // 4142434445
    ` *
     */
    val trimmedHexValue: String
        get() = this.hexValue.split("00".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

    init {
        if (bytes.size < 32) {
            bytes = Util.paddedByteArray(bytes, 32)
        } else if (bytes.size > 32) {
            throw MemoTooLongException("MEMO_HASH can contain 32 bytes at max.")
        }

    }

    constructor(hexString: String) : this(BaseEncoding.base16().lowerCase().decode(hexString.toLowerCase())) {}// We change to lowercase because we want to decode both: upper cased and lower cased alphabets.

    abstract override fun toXdr(): org.stellar.sdk.xdr.Memo

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as MemoHashAbstract?
        return Objects.equal(bytes, that!!.bytes)
    }
}
