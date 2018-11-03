package org.stellar.sdk

import com.google.gson.annotations.SerializedName
import org.stellar.sdk.xdr.Int32
import java.math.BigDecimal
import java.util.*

typealias XDRPrice = org.stellar.sdk.xdr.Price

/**
 * Represents Price. Price in Stellar is represented as a fraction.
 */
data class Price(@field:SerializedName("n") val numerator: Int,
                 @field:SerializedName("d") val denominator: Int) {

    /**
     * Generates Price XDR object.
     */
    fun toXdr(): XDRPrice {
        val xdr = XDRPrice()
        xdr.n = Int32(numerator)
        xdr.d = Int32(denominator)
        return xdr
    }

    companion object {

        /**
         * Approximates `price` to a fraction.
         * Please remember that this function can give unexpected results for values that cannot be represented as a
         * fraction with 32-bit numerator and denominator. It's safer to create a Price object using the constructor.
         * @param price Ex. "1.25"
         */
        @JvmStatic
        fun fromString(price: String): Price {
            val maxInt = BigDecimal(Integer.MAX_VALUE)
            var number = BigDecimal(price)
            var a: BigDecimal
            var f: BigDecimal
            val fractions = ArrayList<Array<BigDecimal>>()
            fractions.add(arrayOf(BigDecimal(0), BigDecimal(1)))
            fractions.add(arrayOf(BigDecimal(1), BigDecimal(0)))
            var i = 2
            while (true) {
                if (number.compareTo(maxInt) > 0) {
                    break
                }
                a = number.setScale(0, BigDecimal.ROUND_FLOOR)
                f = number.subtract(a)
                val h = a.multiply(fractions[i - 1][0]).add(fractions[i - 2][0])
                val k = a.multiply(fractions[i - 1][1]).add(fractions[i - 2][1])
                if (h.compareTo(maxInt) > 0 || k.compareTo(maxInt) > 0) {
                    break
                }
                fractions.add(arrayOf(h, k))
                if (f.compareTo(BigDecimal.ZERO) == 0) {
                    break
                }
                number = BigDecimal(1).divide(f, 20, BigDecimal.ROUND_HALF_UP)
                i = i + 1
            }
            val n = fractions[fractions.size - 1][0]
            val d = fractions[fractions.size - 1][1]
            return Price(n.toInt(), d.toInt())
        }
    }
}