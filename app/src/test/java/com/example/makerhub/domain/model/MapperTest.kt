package com.example.makerhub.domain.model

import com.example.makerhub.common.CheckoutStatus
import com.example.makerhub.common.DateUtils
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class MapperTest {

    @Test
    fun testBorrowerMapping() {
        val entity = BorrowerEntity(
            id = "b-001",
            name = "Maria Dizon",
            email = "maria.dizon@example.com",
            joinedAt = DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00"),
            updatedAt = DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00")
        )
        val domain = entity.toBorrower()
        assertEquals("b-001", domain.id)
        assertEquals("Maria Dizon", domain.name)
        assertEquals("maria.dizon@example.com", domain.email)

        val backToEntity = domain.toEntity()
        assertEquals(entity.id, backToEntity.id)
        assertEquals(entity.name, backToEntity.name)
        assertEquals(entity.email, backToEntity.email)
    }

    @Test
    fun testItemMapping() {
        val entity = ItemEntity(
            id = 101,
            name = "Prusa MK4 3D Printer",
            category = "printer",
            status = "available",
            updatedAt = DateUtils.parseTimestamp("2026-09-01T10:00:00+08:00")
        )
        val domain = entity.toItem()
        assertEquals(101, domain.id)
        assertEquals("Prusa MK4 3D Printer", domain.name)
        assertEquals("printer", domain.category)
        assertEquals(ItemStatus.AVAILABLE, domain.status)

        val backToEntity = domain.toEntity()
        assertEquals(entity.id, backToEntity.id)
        assertEquals(entity.name, backToEntity.name)
        assertEquals(entity.category, backToEntity.category)
        assertEquals(entity.status, backToEntity.status)
    }

    @Test
    fun testCheckoutMapping() {
        val borrowerEntity = BorrowerEntity(
            id = "b-001",
            name = "Maria Dizon",
            email = "maria.dizon@example.com",
            joinedAt = DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00"),
            updatedAt = DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00")
        )
        val itemEntity = ItemEntity(
            id = 102,
            name = "Ender 3 V3 3D Printer",
            category = "printer",
            status = "on_loan",
            updatedAt = DateUtils.parseTimestamp("2026-09-20T09:30:00+08:00")
        )
        val checkoutEntity = CheckoutEntity(
            id = "c-0001",
            itemId = 102,
            borrowerId = "b-001",
            status = "active",
            checkedOutAt = DateUtils.parseTimestamp("2026-09-20T09:30:00+08:00"),
            dueAt = DateUtils.parseTimestamp("2026-09-27T18:00:00+08:00"),
            returnedAt = null,
            updatedAt = DateUtils.parseTimestamp("2026-09-20T09:30:00+08:00")
        )

        val borrower = borrowerEntity.toBorrower()
        val item = itemEntity.toItem()
        val checkout = checkoutEntity.toCheckout(item, borrower)

        assertEquals("c-0001", checkout.id)
        assertEquals(item, checkout.item)
        assertEquals(borrower, checkout.borrower)
        assertEquals(CheckoutStatus.ACTIVE, checkout.status)

        val backToEntity = checkout.toEntity()
        assertEquals(checkoutEntity.id, backToEntity.id)
        assertEquals(checkoutEntity.itemId, backToEntity.itemId)
        assertEquals(checkoutEntity.borrowerId, backToEntity.borrowerId)
        assertEquals(checkoutEntity.status, backToEntity.status)
    }
}
