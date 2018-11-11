package org.stellar.sdk.operation

import org.stellar.sdk.xdr.OperationType

/**
 * Represents [Inflation](https://www.stellar.org/developers/learn/concepts/list-of-operations.html#inflation) operation.
 * @see [List of Operations](https://www.stellar.org/developers/learn/concepts/list-of-operations.html)
 */
class InflationOperation : Operation() {
    internal override fun toOperationBody(): org.stellar.sdk.xdr.Operation.OperationBody {
        val body = org.stellar.sdk.xdr.Operation.OperationBody()
        body.discriminant = OperationType.INFLATION
        return body
    }
}
