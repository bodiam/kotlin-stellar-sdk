package org.stellar.sdk

import java.nio.charset.Charset

/**
 * Network class is used to specify which Stellar network you want to use.
 * Each network has a `networkPassphrase` which is hashed to
 * every transaction id.
 * There is no default network. You need to specify network when initializing your app by calling
 * [Network.use], [Network.usePublicNetwork] or [Network.useTestNetwork].
 *
 * // TODO: See https://github.com/Synesso/scala-stellar-sdk/blob/master/src/main/scala/stellar/sdk/Network.scala
 * // TODO: Network is missing a lot of code, which is in the scala version. Would be nice to add that here too.
 */
class Network(val networkPassphrase: String) {

    /**
     * Returns network id (SHA-256 hashed `networkPassphrase`).
     */
    val networkId: ByteArray
        get() = Util.hash(current!!.networkPassphrase.toByteArray(Charset.forName("UTF-8")))

    companion object {
        private const val PUBLIC = "Public Global Stellar Network ; September 2015"
        private const val TESTNET = "Test SDF Network ; September 2015"
        private var current: Network? = null

        /**
         * Returns currently used Network object.
         */
        @JvmStatic
        fun current(): Network? {
            return current
        }

        /**
         * Use `network` as a current network.
         * @param network Network object to set as current network
         */
        @JvmStatic
        fun use(network: Network?) {
            current = network
        }

        /**
         * Use Stellar Public Network
         */
        @JvmStatic
        fun usePublicNetwork() {
            Network.use(Network(PUBLIC))
        }

        /**
         * Use Stellar Test Network.
         */
        @JvmStatic
        fun useTestNetwork() {
            Network.use(Network(TESTNET))
        }
    }
}
