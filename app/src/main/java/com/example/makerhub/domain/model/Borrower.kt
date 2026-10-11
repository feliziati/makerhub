package com.example.makerhub.domain.model

import com.example.makerhub.data.local.entity.BorrowerEntity

data class Borrower(
    val id: String,
    val name: String,
    val email: String,
    val joinedAt: Long
)

fun BorrowerEntity.toBorrower(): Borrower {
    return Borrower(
        id = id,
        name = name,
        email = email,
        joinedAt = joinedAt
    )
}

fun Borrower.toEntity(updatedAt: Long = System.currentTimeMillis()): BorrowerEntity {
    return BorrowerEntity(
        id = id,
        name = name,
        email = email,
        joinedAt = joinedAt,
        updatedAt = updatedAt
    )
}
