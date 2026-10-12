package com.example.makerhub.data.remote.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class StringDeserializer : JsonDeserializer<String?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String? {
        // Safe check for null or json null elements
        if (json == null || json.isJsonNull) {
            return null
        }

        // Convert to string and trim whitespace
        return json.asString.lowercase().trim()
    }
}