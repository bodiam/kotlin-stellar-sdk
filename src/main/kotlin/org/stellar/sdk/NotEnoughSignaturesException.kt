package org.stellar.sdk

/**
 * Indicates that the object that has to be signed has not enough signatures.
 */
class NotEnoughSignaturesException(message: String) : RuntimeException(message)
