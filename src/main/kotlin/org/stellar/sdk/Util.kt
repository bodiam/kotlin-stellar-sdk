package org.stellar.sdk

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

internal object Util {

    val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

    @JvmStatic
    fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = HEX_ARRAY[v.ushr(4)]
            hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
        }
        return String(hexChars)
    }

    @JvmStatic
    fun hexToBytes(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    /**
     * Returns SHA-256 hash of `data`.
     * @param data
     */
    @JvmStatic
    fun hash(data: ByteArray): ByteArray {
        try {
            val md = MessageDigest.getInstance("SHA-256")
            md.update(data)
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("SHA-256 not implemented")
        }

    }

    /**
     * Pads `bytes` array to `length` with zeros.
     * @param bytes
     * @param length
     */
    @JvmStatic
    fun paddedByteArray(bytes: ByteArray, length: Int): ByteArray {
        val finalBytes = ByteArray(length)
        Arrays.fill(finalBytes, 0.toByte())
        System.arraycopy(bytes, 0, finalBytes, 0, bytes.size)
        return finalBytes
    }

    /**
     * Pads `string` to `length` with zeros.
     * @param string
     * @param length
     */
    @JvmStatic
    fun paddedByteArray(string: String, length: Int): ByteArray {
        return paddedByteArray(string.toByteArray(), length)
    }

    /**
     * Remove zeros from the end of `bytes` array.
     * @param bytes
     */
    @JvmStatic
    fun paddedByteArrayToString(bytes: ByteArray): String {
        return String(bytes)
                .split("\u0000".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[0]
    }
}
