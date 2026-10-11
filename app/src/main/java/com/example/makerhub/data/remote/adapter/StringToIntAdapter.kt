package com.example.makerhub.data.remote.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class StringToIntAdapter : TypeAdapter<Int?>() {
    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    override fun read(reader: JsonReader): Int? {
        return when (reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            JsonToken.NUMBER -> {
                try {
                    reader.nextInt()
                } catch (_: NumberFormatException) {
                    reader.nextDouble().toInt()
                }
            }
            JsonToken.STRING -> {
                val stringValue = reader.nextString().trim()
                stringValue.toIntOrNull()
            }
            JsonToken.BOOLEAN -> {
                reader.nextBoolean()
                null
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }
}
