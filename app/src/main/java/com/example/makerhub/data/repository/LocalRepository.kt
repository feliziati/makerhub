package com.example.makerhub.data.repository

import androidx.lifecycle.LiveData
import com.example.makerhub.data.local.entity.BorrowerEntity
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.local.entity.ItemEntity

interface LocalRepository {
    // Save operations (returns row ID or -1 if insertion failed/ignored)
    suspend fun saveItem(item: ItemEntity): Long
    suspend fun saveItems(items: List<ItemEntity>): LongArray
    suspend fun saveBorrower(borrower: BorrowerEntity): Long
    suspend fun saveBorrowers(borrowers: List<BorrowerEntity>): LongArray
    suspend fun saveCheckout(checkout: CheckoutEntity): Long
    suspend fun saveCheckouts(checkouts: List<CheckoutEntity>): LongArray

    // Item queries
    suspend fun getItemById(id: Int): ItemEntity?
    fun getItemByIdLiveData(id: Int): LiveData<ItemEntity?>
    fun getAllItems(): LiveData<List<ItemEntity>>
    fun getItemsByStatus(status: String): LiveData<List<ItemEntity>>

    // Borrower queries
    suspend fun getBorrowerById(id: String): BorrowerEntity?
    fun getBorrowerByIdLiveData(id: String): LiveData<BorrowerEntity?>
    fun getAllBorrowers(): LiveData<List<BorrowerEntity>>

    // Checkout queries
    suspend fun getCheckoutById(id: String): CheckoutEntity?
    fun getCheckoutByIdLiveData(id: String): LiveData<CheckoutEntity?>
    fun getAllCheckouts(): LiveData<List<CheckoutEntity>>
    fun getCheckoutsByStatus(status: String): LiveData<List<CheckoutEntity>>
    fun getCheckoutsByBorrower(borrowerId: String): LiveData<List<CheckoutEntity>>
    fun getCheckoutsByItem(itemId: Int): LiveData<List<CheckoutEntity>>
}
