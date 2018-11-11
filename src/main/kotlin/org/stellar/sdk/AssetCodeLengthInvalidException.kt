package org.stellar.sdk

/**
 * Indicates that asset code is not valid for a specified asset class
 * @see IssuedAsset4
 *
 * @see IssuedAsset12
 */
class AssetCodeLengthInvalidException : RuntimeException {
    constructor() : super()

    constructor(message: String) : super(message)
}