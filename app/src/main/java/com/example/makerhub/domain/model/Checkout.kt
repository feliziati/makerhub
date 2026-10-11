package com.example.makerhub.domain.model

import com.example.makerhub.data.local.entity.CheckoutEntity

data class Checkout(
    val id: String,
    val item: Item,
    val borrower: Borrower,
    val status: CheckoutStatus,
    val checkedOutAt: Long,
    val dueAt: Long,
    val returnedAt: Long?,
    val updatedAt: Long
)

enum class CheckoutStatus {
    ACTIVE, RETURNED, LOST
}

fun CheckoutEntity.toCheckout(item: Item, borrower: Borrower): Checkout {
    val checkoutStatus = parseCheckoutStatus(status)
    return Checkout(
        id = id,
        item = item,
        borrower = borrower,
        status = checkoutStatus,
        checkedOutAt = checkedOutAt,
        dueAt = dueAt,
        returnedAt = returnedAt,
        updatedAt = updatedAt
    )
}

fun Checkout.toEntity(): CheckoutEntity {
    return CheckoutEntity(
        id = id,
        itemId = item.id,
        borrowerId = borrower.id,
        status = status.toEntityString(),
        checkedOutAt = checkedOutAt,
        dueAt = dueAt,
        returnedAt = returnedAt,
        updatedAt = updatedAt
    )
}

fun parseCheckoutStatus(status: String): CheckoutStatus {
    return when (status.lowercase()) {
        "active" -> CheckoutStatus.ACTIVE
        "returned" -> CheckoutStatus.RETURNED
        "lost" -> CheckoutStatus.LOST
        else -> CheckoutStatus.ACTIVE
    }
}

fun CheckoutStatus.toEntityString(): String {
    return when (this) {
        CheckoutStatus.ACTIVE -> "active"
        CheckoutStatus.RETURNED -> "returned"
        CheckoutStatus.LOST -> "lost"
    }
}
