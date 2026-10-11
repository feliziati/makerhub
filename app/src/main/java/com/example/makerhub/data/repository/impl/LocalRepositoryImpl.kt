package com.example.makerhub.data.repository.impl

import androidx.lifecycle.LiveData
import com.example.makerhub.data.local.dao.BorrowerDao
import com.example.makerhub.data.local.dao.CheckoutDao
import com.example.makerhub.data.local.dao.ItemDao
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import com.example.makerhub.data.repository.LocalRepository

class LocalRepositoryImpl(
    private val itemDao: ItemDao,
    private val borrowerDao: BorrowerDao,
    private val checkoutDao: CheckoutDao
) : LocalRepository {

    override suspend fun saveItem(item: ItemEntity): Long {
        return itemDao.upsert(item)
    }

    override suspend fun saveItems(items: List<ItemEntity>): LongArray {
        return itemDao.insertAll(items)
    }

    override suspend fun saveBorrower(borrower: BorrowerEntity): Long {
        return borrowerDao.upsert(borrower)
    }

    override suspend fun saveBorrowers(borrowers: List<BorrowerEntity>): LongArray {
        return borrowerDao.insertAll(borrowers)
    }

    override suspend fun saveCheckout(checkout: CheckoutEntity): Long {
        return checkoutDao.upsert(checkout)
    }

    override suspend fun saveCheckouts(checkouts: List<CheckoutEntity>): LongArray {
        return checkoutDao.insertAll(checkouts)
    }

    override suspend fun getItemById(id: Int): ItemEntity? = itemDao.getItemById(id)

    override fun getItemByIdLiveData(id: Int): LiveData<ItemEntity?> = itemDao.getItemByIdLiveData(id)

    override fun getAllItems(): LiveData<List<ItemEntity>> = itemDao.getAllItems()

    override fun getItemsByStatus(status: String): LiveData<List<ItemEntity>> = itemDao.getItemsByStatus(status)

    override suspend fun getBorrowerById(id: String): BorrowerEntity? = borrowerDao.getBorrowerById(id)

    override fun getBorrowerByIdLiveData(id: String): LiveData<BorrowerEntity?> = borrowerDao.getBorrowerByIdLiveData(id)

    override fun getAllBorrowers(): LiveData<List<BorrowerEntity>> = borrowerDao.getAllBorrowers()

    override suspend fun getCheckoutById(id: String): CheckoutEntity? = checkoutDao.getCheckoutById(id)

    override fun getCheckoutByIdLiveData(id: String): LiveData<CheckoutEntity?> = checkoutDao.getCheckoutByIdLiveData(id)

    override fun getAllCheckouts(): LiveData<List<CheckoutEntity>> = checkoutDao.getAllCheckouts()

    override fun getCheckoutsByStatus(status: String): LiveData<List<CheckoutEntity>> = checkoutDao.getCheckoutsByStatus(status)

    override fun getCheckoutsByBorrower(borrowerId: String): LiveData<List<CheckoutEntity>> = checkoutDao.getCheckoutsByBorrower(borrowerId)

    override fun getCheckoutsByItem(itemId: Int): LiveData<List<CheckoutEntity>> = checkoutDao.getCheckoutsByItem(itemId)
}
