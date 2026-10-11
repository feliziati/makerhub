package com.example.makerhub.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.makerhub.data.local.entity.CheckoutEntity

@Dao
interface CheckoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkout: CheckoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkouts: List<CheckoutEntity>): LongArray

    @Transaction
    suspend fun upsert(checkout: CheckoutEntity): Long {
        val existing = getCheckoutById(checkout.id)
        return if (existing == null) {
            insert(checkout)
        } else {
            if (checkout.updatedAt > existing.updatedAt) {
                update(checkout)
            }
            -1L
        }
    }

    @Transaction
    suspend fun upsertAll(checkouts: List<CheckoutEntity>): LongArray {
        return checkouts.map { upsert(it) }.toLongArray()
    }

    @Update
    suspend fun update(checkout: CheckoutEntity)

    @Delete
    suspend fun delete(checkout: CheckoutEntity)

    @Query("SELECT * FROM checkouts WHERE id = :id")
    suspend fun getCheckoutById(id: String): CheckoutEntity?

    @Query("SELECT * FROM checkouts WHERE id = :id")
    fun getCheckoutByIdLiveData(id: String): LiveData<CheckoutEntity?>

    @Query("SELECT * FROM checkouts")
    fun getAllCheckouts(): LiveData<List<CheckoutEntity>>

    @Query("SELECT * FROM checkouts WHERE status = :status")
    fun getCheckoutsByStatus(status: String): LiveData<List<CheckoutEntity>>

    @Query("SELECT * FROM checkouts WHERE borrowerId = :borrowerId")
    fun getCheckoutsByBorrower(borrowerId: String): LiveData<List<CheckoutEntity>>

    @Query("SELECT * FROM checkouts WHERE borrowerId = :itemId")
    fun getCheckoutsByItem(itemId: Int): LiveData<List<CheckoutEntity>>
}
