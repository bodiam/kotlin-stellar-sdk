package org.stellar.sdk.responses

import com.google.common.io.BaseEncoding
import com.google.gson.*
import org.stellar.sdk.KeyPair
import org.stellar.sdk.memo.Memo

import java.lang.reflect.Type

class TransactionDeserializer : JsonDeserializer<TransactionResponse> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): TransactionResponse {
        // Create new Gson object with adapters needed in Transaction
        val gson = GsonBuilder()
                .registerTypeAdapter(KeyPair::class.java, KeyPairTypeAdapter().nullSafe())
                .create()

        val transaction = gson.fromJson(json, TransactionResponse::class.java)

        val memoType = json.asJsonObject.get("memo_type").asString
        val memo: Memo
        if (memoType == "none") {
            memo = Memo.none()
        } else {
            // Because of the way "encoding/json" works on structs in Go, if transaction
            // has an empty `memo_text` value, the `memo` field won't be present in a JSON
            // representation of a transaction. That's why we need to handle a special case
            // here.
            if (memoType == "text") {
                val memoField = json.asJsonObject.get("memo")
                if (memoField != null) {
                    memo = Memo.text(memoField.asString)
                } else {
                    memo = Memo.text("")
                }
            } else {
                val memoValue = json.asJsonObject.get("memo").asString
                val base64Encoding = BaseEncoding.base64()
                if (memoType == "id") {
                    memo = Memo.id(java.lang.Long.parseUnsignedLong(memoValue))
                } else if (memoType == "hash") {
                    memo = Memo.hash(base64Encoding.decode(memoValue))
                } else if (memoType == "return") {
                    memo = Memo.returnHash(base64Encoding.decode(memoValue))
                } else {
                    throw JsonParseException("Unknown memo type.")
                }
            }
        }

        transaction.memo = memo
        return transaction
    }
}
