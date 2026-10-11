package com.example.makerhub.common

import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

object DateUtils {
    fun parseIsoToMillis(isoString: String?): Long {
        if (isoString.isNullOrBlank()) throw Exception("Timestamp is blank or null")
        return try {
            OffsetDateTime.parse(isoString).toInstant().toEpochMilli()
        } catch (e: DateTimeParseException) {
            throw Exception("Unable to parse $isoString to millis")
        }
    }

    fun parseIsoToMillisOrNull(isoString: String?): Long? {
        if (isoString.isNullOrBlank()) return 0L
        return try {
            OffsetDateTime.parse(isoString).toInstant().toEpochMilli()
        } catch (e: DateTimeParseException) {
            isoString.toLongOrNull()
        }
    }

    fun parseTimestamp(any: Any?): Long {
        if (any == null) throw Exception("Timestamp is null")
        return if (any is Long) {
            if (isValidTimestamp(any)) {
                 any
            } else {
                throw Exception("$any is invalid timestamp")
            }
        } else if (any is String) {
            parseIsoToMillis(any)
        } else {
            throw Exception("Unable to parse $any to millis")
        }
    }

    fun isValidTimestamp(timestamp: Long): Boolean {
        val minValid = 0L // 1970-01-01
        val maxValid = 4102444800000L // 2100-01-01

        return timestamp in minValid..maxValid
    }
}
