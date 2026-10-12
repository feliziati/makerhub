package com.example.makerhub.data.repository.impl

import androidx.lifecycle.LiveData
import com.example.makerhub.data.local.AppDatabase
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity
import com.example.makerhub.data.remote.response.BorrowerResponse
import com.example.makerhub.data.remote.response.CheckoutResponse
import com.example.makerhub.data.remote.response.ItemResponse
import com.example.makerhub.data.remote.response.validateFields
import com.example.makerhub.data.remote.response.validatePartialFields
import com.example.makerhub.data.repository.LocalRepository
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : LocalRepository {

    private val itemDao get() = db.itemDao()
    private val borrowerDao get() = db.borrowerDao()
    private val checkoutDao get() = db.checkoutDao()

    override suspend fun saveItem(item: ItemEntity): Long {
        return itemDao.upsert(item)
    }

    override suspend fun saveItem(itemResponse: ItemResponse): Long {
        itemResponse.validateFields()
        val item = ItemEntity.from(
            itemResponse.id,
            itemResponse.name!!,
            itemResponse.category!!,
            itemResponse.status!!,
            itemResponse.updatedAt!!
        )
        return saveItem(item)
    }

    override suspend fun saveItems(items: List<ItemEntity>): LongArray {
        return itemDao.insertAll(items)
    }

    override suspend fun saveBorrower(borrower: BorrowerEntity): Long {
        return borrowerDao.upsert(borrower)
    }

    override suspend fun saveBorrower(borrowerResponse: BorrowerResponse): Long {
        borrowerResponse.validateFields()
        val borrower = BorrowerEntity.from(
            borrowerResponse.id ?: "",
            borrowerResponse.name ?: "",
            borrowerResponse.email ?: "",
            borrowerResponse.joinedAt ?: "",
            borrowerResponse.updatedAt
        )
        return saveBorrower(borrower)
    }

    override suspend fun saveBorrowers(borrowers: List<BorrowerEntity>): LongArray {
        return borrowerDao.insertAll(borrowers)
    }

    override suspend fun saveCheckout(checkout: CheckoutEntity): Long {
        return checkoutDao.upsert(checkout)
    }

    override suspend fun saveCheckout(checkoutResponse: CheckoutResponse): Long {
        val existing = getCheckoutById(checkoutResponse.id!!)
        return if (existing == null) {
            checkoutResponse.validateFields()
            val entity = CheckoutEntity.from(
                checkoutResponse.id,
                checkoutResponse.itemId ?: -1,
                checkoutResponse.borrowerId ?: "",
                checkoutResponse.status ?: "",
                checkoutResponse.checkedOutAt ?:"",
                checkoutResponse.dueAt ?: "",
                checkoutResponse.returnedAt,
                checkoutResponse.updatedAt ?: ""
            )
            checkoutDao.upsert(entity)
        } else {
            checkoutResponse.validatePartialFields()
            checkoutDao.applyDelta(checkoutResponse)
        }
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

    override suspend fun getCheckoutsByItemSync(itemId: Int): List<CheckoutEntity> = checkoutDao.getCheckoutsByItemSync(itemId)
}
