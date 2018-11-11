package org.stellar.sdk.memo

import com.google.common.io.BaseEncoding
import org.stellar.sdk.xdr.MemoType
import org.stellar.sdk.xdr.MemoType.*

/**
 *
 * The memo contains optional extra information. It is the responsibility of the client to interpret this value. Memos can be one of the following types:
 *
 *  * `MEMO_NONE`: Empty memo.
 *  * `MEMO_TEXT`: A string up to 28-bytes long.
 *  * `MEMO_ID`: A 64 bit unsigned integer.
 *  * `MEMO_HASH`: A 32 byte hash.
 *  * `MEMO_RETURN`: A 32 byte hash intended to be interpreted as the hash of the transaction the sender is refunding.
 *
 *
 * Use static methods to generate any of above types.
 * @see Transaction
 */
abstract class Memo {

    abstract fun toXdr(): org.stellar.sdk.xdr.Memo
    abstract override fun equals(other: Any?): Boolean


    companion object {
        /**
         * Creates new MemoNone instance.
         */
        @JvmStatic
        fun none(): MemoNone {
            return MemoNone()
        }

        /**
         * Creates new [MemoText] instance.
         * @param text
         */
        @JvmStatic
        fun text(text: String): MemoText {
            return MemoText(text)
        }

        /**
         * Creates new [MemoId] instance.
         * @param id
         */
        @JvmStatic
        fun id(id: Long): MemoId {
            return MemoId(id)
        }

        /**
         * Creates new [MemoHash] instance from byte array.
         * @param bytes
         */
        @JvmStatic
        fun hash(bytes: ByteArray): MemoHash {
            return MemoHash(bytes)
        }

        /**
         * Creates new [MemoHash] instance from hex-encoded string
         * @param hexString
         */
        @JvmStatic
        fun hash(hexString: String): MemoHash {
            return MemoHash(hexString)
        }

        /**
         * Creates new [MemoReturnHash] instance from byte array.
         * @param bytes
         */
        @JvmStatic
        fun returnHash(bytes: ByteArray): MemoReturnHash {
            return MemoReturnHash(bytes)
        }

        /**
         * Creates new [MemoReturnHash] instance from hex-encoded string.
         * @param hexString
         */
        @JvmStatic
        fun returnHash(hexString: String): MemoReturnHash {
            // We change to lowercase because we want to decode both: upper cased and lower cased alphabets.
            return MemoReturnHash(BaseEncoding.base16().lowerCase().decode(hexString.toLowerCase()))
        }

        @JvmStatic
        fun fromXdr(memo: org.stellar.sdk.xdr.Memo): Memo {
            return when (memo.discriminant) {
                MEMO_NONE -> none()
                MEMO_ID -> id(memo.id.uint64!!.toLong())
                MEMO_TEXT -> text(memo.text)
                MEMO_HASH -> hash(memo.hash.hash)
                MEMO_RETURN -> returnHash(memo.retHash.hash)
                else -> throw RuntimeException("Unknown memo type")
            }
        }
    }
}
