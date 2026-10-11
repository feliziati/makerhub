package com.example.makerhub.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.makerhub.data.local.entity.BorrowerEntity

@Dao
interface BorrowerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(borrower: BorrowerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(borrowers: List<BorrowerEntity>): LongArray

    @Transaction
    suspend fun upsert(borrower: BorrowerEntity): Long {
        val existing = getBorrowerById(borrower.id)
        return if (existing == null) {
            insert(borrower)
        } else {
            val existingTime = existing.updatedAt ?: existing.joinedAt
            val newTime = borrower.updatedAt ?: borrower.joinedAt
            if (newTime > existingTime) {
                update(borrower)
            }
            -1L
        }
    }

    @Transaction
    suspend fun upsertAll(borrowers: List<BorrowerEntity>): LongArray {
        return borrowers.map { upsert(it) }.toLongArray()
    }

    @Update
    suspend fun update(borrower: BorrowerEntity)

    @Delete
    suspend fun delete(borrower: BorrowerEntity)

    @Query("SELECT * FROM borrowers WHERE id = :id")
    suspend fun getBorrowerById(id: String): BorrowerEntity?

    @Query("SELECT * FROM borrowers WHERE id = :id")
    fun getBorrowerByIdLiveData(id: String): LiveData<BorrowerEntity?>

    @Query("SELECT * FROM borrowers")
    fun getAllBorrowers(): LiveData<List<BorrowerEntity>>
}
