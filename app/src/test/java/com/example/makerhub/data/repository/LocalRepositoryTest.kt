package com.example.makerhub.data.repository

import com.example.makerhub.common.DateUtils
import com.example.makerhub.data.local.dao.BorrowerDao
import com.example.makerhub.data.local.dao.CheckoutDao
import com.example.makerhub.data.local.dao.ItemDao
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import com.example.makerhub.data.repository.impl.LocalRepositoryImpl
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LocalRepositoryTest {

    private lateinit var itemDao: ItemDao
    private lateinit var borrowerDao: BorrowerDao
    private lateinit var checkoutDao: CheckoutDao
    private lateinit var repository: LocalRepository

    @Before
    fun setUp() {
        itemDao = mockk(relaxed = true)
        borrowerDao = mockk(relaxed = true)
        checkoutDao = mockk(relaxed = true)
        repository = LocalRepositoryImpl(itemDao, borrowerDao, checkoutDao)
    }

    @Test
    fun testSaveItem() = runBlocking {
        val item = ItemEntity(1, "Printer", "printer", "available", DateUtils.parseTimestamp("2026-09-01T10:00:00+08:00"))
        repository.saveItem(item)
        coVerify { itemDao.upsert(item) }
    }

    @Test
    fun testSaveItems() = runBlocking {
        val items = listOf(ItemEntity(1, "Printer", "printer", "available", DateUtils.parseTimestamp("2026-09-01T10:00:00+08:00")))
        repository.saveItems(items)
        coVerify { itemDao.insertAll(items) }
    }

    @Test
    fun testSaveBorrower() = runBlocking {
        val borrower = BorrowerEntity("b-001", "Maria", "maria@example.com", DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00"), DateUtils.parseTimestamp("2026-01-12T10:00:00+08:00"))
        repository.saveBorrower(borrower)
        coVerify { borrowerDao.upsert(borrower) }
    }

    @Test
    fun testSaveCheckout() = runBlocking {
        val checkout = CheckoutEntity("c-0001", 1, "b-001", "active", DateUtils.parseTimestamp("2026-09-20T09:30:00+08:00"), DateUtils.parseTimestamp("2026-09-27T18:00:00+08:00"), null, DateUtils.parseTimestamp("2026-09-20T09:30:00+08:00"))
        repository.saveCheckout(checkout)
        coVerify { checkoutDao.upsert(checkout) }
    }

    @Test
    fun testGetItemById() = runBlocking {
        repository.getItemById(1)
        coVerify { itemDao.getItemById(1) }
    }

    @Test
    fun testGetBorrowerById() = runBlocking {
        repository.getBorrowerById("b-001")
        coVerify { borrowerDao.getBorrowerById("b-001") }
    }

    @Test
    fun testGetCheckoutById() = runBlocking {
        repository.getCheckoutById("c-0001")
        coVerify { checkoutDao.getCheckoutById("c-0001") }
    }
}
