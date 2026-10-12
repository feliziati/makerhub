package com.example.makerhub.common

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Date
import java.util.Locale

object DateUtils {

    fun Long.formatMillisToISO(): String {
        return Instant.ofEpochMilli(this).toString()
    }

    fun Long.formatMillisToDatetime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(this))
    }

    fun parseIsoToMillis(isoString: String?): Long {
        return parseStringToMillis(isoString)
    }

    fun parseIsoToMillisOrNull(isoString: String?): Long? {
        if (isoString.isNullOrBlank()) return 0L
        return try {
            parseStringToMillis(isoString)
        } catch (e: Exception) {
            isoString.toLongOrNull()
        }
    }

    fun parseStringToMillis(dateString: String?): Long {
        if (dateString.isNullOrBlank()) throw IllegalArgumentException("Date string is blank or null")

        // 1. OffsetDateTime (e.g. 2026-09-28T10:00:00+08:00)
        try {
            return OffsetDateTime.parse(dateString).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 2. LocalDateTime (e.g. 2026-09-30T08:00:00)
        try {
            return java.time.LocalDateTime.parse(dateString).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 3. LocalDate (e.g. 2026-09-30)
        try {
            return java.time.LocalDate.parse(dateString).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (_: Exception) {}

        // 4. Formatted date strings like "03/09/2026" (dd/MM/yyyy)
        val patterns = listOf("dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "dd-MM-yyyy")
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault())
                sdf.isLenient = false
                val parsedDate = sdf.parse(dateString)
                if (parsedDate != null) return parsedDate.time
            } catch (_: Exception) {}
        }

        // 5. Epoch millis numeric string
        dateString.toLongOrNull()?.let { return it }

        throw IllegalArgumentException("Unable to parse date string: $dateString")
    }

    fun parseTimestamp(any: Any?): Long {
        if (any == null) throw IllegalArgumentException("Timestamp is null")
        return when (any) {
            is Number -> {
                val longVal = any.toLong()
                if (isValidTimestamp(longVal)) longVal else throw IllegalArgumentException("$any is invalid timestamp")
            }
            is String -> parseStringToMillis(any)
            else -> throw IllegalArgumentException("Unable to parse $any to millis")
        }
    }

    fun parseTimestampOrNull(any: Any?): Long? {
        return try {
            parseTimestamp(any)
        } catch (e: Exception) {
            null
        }
    }

    fun isValidTimestamp(timestamp: Long): Boolean {
        val minValid = 0L // 1970-01-01
        val maxValid = 4102444800000L // 2100-01-01

        return timestamp in minValid..maxValid
    }
}
