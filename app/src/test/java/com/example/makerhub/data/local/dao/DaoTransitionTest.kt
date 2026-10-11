package com.example.makerhub.data.local.dao

import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DaoTransitionTest {

    private class FakeItemDao : ItemDao {
        val items = mutableMapOf<Int, ItemEntity>()

        override suspend fun insert(item: ItemEntity): Long {
            items[item.id] = item
            return item.id.toLong()
        }

        override suspend fun insertAll(items: List<ItemEntity>): LongArray {
            items.forEach { insert(it) }
            return items.map { it.id.toLong() }.toLongArray()
        }

        override suspend fun update(item: ItemEntity) {
            items[item.id] = item
        }

        override suspend fun delete(item: ItemEntity) {
            items.remove(item.id)
        }

        override suspend fun getItemById(id: Int): ItemEntity? = items[id]

        override fun getItemByIdLiveData(id: Int) = throw UnsupportedOperationException()
        override fun getAllItems() = throw UnsupportedOperationException()
        override fun getItemsByStatus(status: String) = throw UnsupportedOperationException()
    }

    private class FakeCheckoutDao : CheckoutDao {
        val checkouts = mutableMapOf<String, CheckoutEntity>()

        override suspend fun insert(checkout: CheckoutEntity): Long {
            checkouts[checkout.id] = checkout
            return 1L
        }

        override suspend fun insertAll(checkouts: List<CheckoutEntity>): LongArray {
            checkouts.forEach { insert(it) }
            return LongArray(checkouts.size) { 1L }
        }

        override suspend fun update(checkout: CheckoutEntity) {
            checkouts[checkout.id] = checkout
        }

        override suspend fun delete(checkout: CheckoutEntity) {
            checkouts.remove(checkout.id)
        }

        override suspend fun getCheckoutById(id: String): CheckoutEntity? = checkouts[id]

        override fun getCheckoutByIdLiveData(id: String) = throw UnsupportedOperationException()
        override fun getAllCheckouts() = throw UnsupportedOperationException()
        override fun getCheckoutsByStatus(status: String) = throw UnsupportedOperationException()
        override fun getCheckoutsByBorrower(borrowerId: String) = throw UnsupportedOperationException()
        override fun getCheckoutsByItem(itemId: Int) = throw UnsupportedOperationException()
    }

    @Test
    fun `item transition from available to on_loan succeeds`() = runBlocking {
        val dao = FakeItemDao()
        val initial = ItemEntity(1, "Drill", "tool", "available", 1000L)
        dao.upsert(initial)

        val updated = ItemEntity(1, "Drill", "tool", "on_loan", 2000L)
        dao.upsert(updated)

        assertEquals("on_loan", dao.getItemById(1)?.status)
    }

    @Test
    fun `item transition from lost to available fails with exception`() = runBlocking {
        val dao = FakeItemDao()
        val initial = ItemEntity(1, "Drill", "tool", "lost", 1000L)
        dao.upsert(initial)

        val invalid = ItemEntity(1, "Drill", "tool", "available", 2000L)
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { dao.upsert(invalid) }
        }
        assertEquals("Cannot transition Item 1 from LOST to AVAILABLE", exception.message)
    }

    @Test
    fun `item transition from available to retired succeeds`() = runBlocking {
        val dao = FakeItemDao()
        val initial = ItemEntity(1, "Drill", "tool", "available", 1000L)
        dao.upsert(initial)

        val updated = ItemEntity(1, "Drill", "tool", "retired", 2000L)
        dao.upsert(updated)

        assertEquals("retired", dao.getItemById(1)?.status)
    }

    @Test
    fun `item transition from lost to retired fails with exception`() = runBlocking {
        val dao = FakeItemDao()
        val initial = ItemEntity(1, "Drill", "tool", "lost", 1000L)
        dao.upsert(initial)

        val invalid = ItemEntity(1, "Drill", "tool", "retired", 2000L)
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { dao.upsert(invalid) }
        }
        assertEquals("Cannot transition Item 1 from LOST to RETIRED", exception.message)
    }

    @Test
    fun `checkout transition from active to returned succeeds`() = runBlocking {
        val dao = FakeCheckoutDao()
        val initial = CheckoutEntity("c1", 1, "b1", "active", 1000L, 2000L, null, 1000L)
        dao.upsert(initial)

        val updated = CheckoutEntity("c1", 1, "b1", "returned", 1000L, 2000L, 1500L, 2000L)
        dao.upsert(updated)

        assertEquals("returned", dao.getCheckoutById("c1")?.status)
    }

    @Test
    fun `checkout transition from returned to active fails with exception`() = runBlocking {
        val dao = FakeCheckoutDao()
        val initial = CheckoutEntity("c1", 1, "b1", "returned", 1000L, 2000L, 1500L, 1000L)
        dao.upsert(initial)

        val invalid = CheckoutEntity("c1", 1, "b1", "active", 1000L, 2000L, null, 2000L)
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking { dao.upsert(invalid) }
        }
        assertEquals("Cannot transition Checkout c1 from RETURNED to ACTIVE", exception.message)
    }
}
