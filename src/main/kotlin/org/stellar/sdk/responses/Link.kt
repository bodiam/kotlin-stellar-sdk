package org.stellar.sdk.responses

import com.google.gson.annotations.SerializedName

import java.net.URI
import java.net.URISyntaxException

/**
 * Represents links in responses.
 */
class Link(@field:SerializedName("href")
           // TODO templated
           val href: String, @field:SerializedName("templated")
           val isTemplated: Boolean) {

    // TODO templated
    val uri: URI
        get() {
            try {
                return URI(href)
            } catch (e: URISyntaxException) {
                throw RuntimeException(e)
            }

        }
}
