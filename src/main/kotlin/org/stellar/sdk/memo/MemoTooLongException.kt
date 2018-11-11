package org.stellar.sdk.memo

/**
 * Indicates that value passed to Memo
 * @see Memo
 */
class MemoTooLongException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)
}
