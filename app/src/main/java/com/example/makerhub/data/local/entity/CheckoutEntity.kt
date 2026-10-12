package com.example.makerhub.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.makerhub.common.DateUtils

@Entity(tableName = "checkouts")
data class CheckoutEntity(
    @PrimaryKey
    val id: String,
    val itemId: Int,
    val borrowerId: String,
    val status: String,
    val checkedOutAt: Long,
    val dueAt: Long,
    val returnedAt: Long? = null,
    val updatedAt: Long
) {
    companion object {
        fun from(
            id: String,
            itemId: Int,
            borrowerId: String,
            status: String,
            checkedOutAt: String,
            dueAt: String,
            returnedAt: String?,
            updatedAt: String
        ): CheckoutEntity {
            return CheckoutEntity(
                id = id,
                itemId = itemId,
                borrowerId = borrowerId,
                status = status,
                checkedOutAt = DateUtils.parseTimestamp(checkedOutAt),
                dueAt = DateUtils.parseTimestamp(dueAt),
                returnedAt = DateUtils.parseTimestampOrNull(returnedAt),
                updatedAt = DateUtils.parseTimestamp(updatedAt)
            )
        }
    }
}


