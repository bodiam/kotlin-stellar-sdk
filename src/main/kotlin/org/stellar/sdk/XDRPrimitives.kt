@file:Suppress("FunctionName")

package org.stellar.sdk

import org.stellar.sdk.xdr.Int32

fun Int32(value: Int): Int32 = Int32().apply { int32 = value }
