package org.stellar.sdk.responses

import okhttp3.Headers

abstract class Response {
    /**
     * Returns X-RateLimit-Limit header from the response.
     * This number represents the he maximum number of requests that the current client can
     * make in one hour.
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitLimit: Int = 0
        protected set
    /**
     * Returns X-RateLimit-Remaining header from the response.
     * The number of remaining requests for the current window.
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitRemaining: Int = 0
        protected set
    /**
     * Returns X-RateLimit-Reset header from the response. Seconds until a new window starts.
     * @see [Rate Limiting](https://www.stellar.org/developers/horizon/learn/rate-limiting.html)
     */
    var rateLimitReset: Int = 0
        protected set

    fun setHeaders(headers: Headers) {
        if (headers.get("X-Ratelimit-Limit") != null) {
            this.rateLimitLimit = Integer.parseInt(headers.get("X-Ratelimit-Limit")!!)
        }
        if (headers.get("X-Ratelimit-Remaining") != null) {
            this.rateLimitRemaining = Integer.parseInt(headers.get("X-Ratelimit-Remaining")!!)
        }
        if (headers.get("X-Ratelimit-Reset") != null) {
            this.rateLimitReset = Integer.parseInt(headers.get("X-Ratelimit-Reset")!!)
        }
    }
}
