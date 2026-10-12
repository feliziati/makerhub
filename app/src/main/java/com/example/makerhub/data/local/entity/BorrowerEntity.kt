package com.example.makerhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.makerhub.common.DateUtils

@Entity(tableName = "borrowers")
data class BorrowerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val joinedAt: Long,
    val updatedAt: Long? = null
) {
    companion object {
        fun from(id: String, name: String, email: String, joinedAt: String?, updatedAt: String? = null): BorrowerEntity {
            return BorrowerEntity(
                id = id,
                name = name,
                email = email,
                joinedAt = DateUtils.parseTimestamp(joinedAt),
                updatedAt = DateUtils.parseTimestampOrNull(updatedAt)
            )
        }

        fun from(id: String, name: String, email: String, joinedAt: Long, updatedAt: Long? = null): BorrowerEntity {
            return BorrowerEntity(
                id = id,
                name = name,
                email = email,
                joinedAt = joinedAt,
                updatedAt = updatedAt
            )
        }
    }
}
