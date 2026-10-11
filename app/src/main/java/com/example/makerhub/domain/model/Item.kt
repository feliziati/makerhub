package com.example.makerhub.domain.model

import com.example.makerhub.data.local.entity.ItemEntity

data class Item(
    val id: Int,
    val name: String,
    val category: String,
    val status: ItemStatus,
    val updatedAt: Long
)

enum class ItemStatus {
    AVAILABLE, ON_LOAN, LOST, RETIRED
}

fun ItemEntity.toItem(): Item {
    val itemStatus = parseItemStatus(status)
    return Item(
        id = id,
        name = name,
        category = category,
        status = itemStatus,
        updatedAt = updatedAt
    )
}

fun Item.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        name = name,
        category = category,
        status = status.toEntityString(),
        updatedAt = updatedAt
    )
}

fun parseItemStatus(status: String): ItemStatus {
    return when (status.lowercase()) {
        "available" -> ItemStatus.AVAILABLE
        "on_loan" -> ItemStatus.ON_LOAN
        "lost" -> ItemStatus.LOST
        "retired" -> ItemStatus.RETIRED
        else -> ItemStatus.AVAILABLE
    }
}

fun ItemStatus.toEntityString(): String {
    return when (this) {
        ItemStatus.AVAILABLE -> "available"
        ItemStatus.ON_LOAN -> "on_loan"
        ItemStatus.LOST -> "lost"
        ItemStatus.RETIRED -> "retired"
    }
}
