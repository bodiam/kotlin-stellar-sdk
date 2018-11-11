package org.stellar.sdk

import org.stellar.sdk.xdr.Uint64

/**
 *
 * TimeBounds represents the time interval that a transaction is valid.
 * @see Transaction
 */
class TimeBounds(private val minTime: Long, private val maxTime: Long) {

    init {
        if (maxTime in 1..minTime) {
            throw IllegalArgumentException("minTime must be >= maxTime")
        }
    }

    fun toXdr(): org.stellar.sdk.xdr.TimeBounds {
        val timeBounds = org.stellar.sdk.xdr.TimeBounds()
        val minTime = Uint64()
        val maxTime = Uint64()
        minTime.uint64 = this.minTime
        maxTime.uint64 = this.maxTime
        timeBounds.minTime = minTime
        timeBounds.maxTime = maxTime
        return timeBounds
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as TimeBounds

        return if (minTime != that.minTime) false else maxTime == that.maxTime
    }

    companion object {

        fun fromXdr(timeBounds: org.stellar.sdk.xdr.TimeBounds?): TimeBounds? {
            return if (timeBounds == null) {
                null
            } else TimeBounds(
                    timeBounds.minTime.uint64.toLong(),
                    timeBounds.maxTime.uint64.toLong()
            )

        }
    }
}
