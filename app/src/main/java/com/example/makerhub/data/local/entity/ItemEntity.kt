package com.example.makerhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.makerhub.common.DateUtils

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val category: String,
    val status: String,
    val updatedAt: Long
) {
    companion object {
        fun from(id: Any?, name: String, category: String, status: String, updatedAt: String?): ItemEntity {
            val parsedId = when (id) {
                is Number -> id.toInt()
                is String -> id.toIntOrNull() ?: 0
                else -> 0
            }
            return ItemEntity(
                id = parsedId,
                name = name.trim(),
                category = category.trim(),
                status = status.trim(),
                updatedAt = DateUtils.parseTimestamp(updatedAt)
            )
        }

        fun from(id: Int, name: String, category: String, status: String, updatedAt: Long): ItemEntity {
            return ItemEntity(
                id = id,
                name = name.trim(),
                category = category.trim(),
                status = status.trim(),
                updatedAt = updatedAt
            )
        }
    }
}
