package com.example.makerhub.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.makerhub.common.isItemStatusValid
import com.example.makerhub.data.local.entity.ItemEntity
import com.example.makerhub.domain.model.parseItemStatus

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ItemEntity>): LongArray

    @Transaction
    suspend fun upsert(item: ItemEntity): Long {
        val newStatus = parseItemStatus(item.status)
        val existing = getItemById(item.id)
        return if (existing == null) {
            insert(item)
        } else {
            if (item.updatedAt > existing.updatedAt) {
                val oldStatus = parseItemStatus(existing.status)
                if (oldStatus != newStatus && !isItemStatusValid(oldStatus, newStatus)) {
                    throw IllegalArgumentException("Cannot transition Item ${item.id} from $oldStatus to $newStatus")
                }
                update(item)
                item.id.toLong()
            } else {
                -1L
            }
        }
    }

    @Transaction
    suspend fun upsertAll(items: List<ItemEntity>): LongArray {
        return items.map { upsert(it) }.toLongArray()
    }

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Int): ItemEntity?

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemByIdLiveData(id: Int): LiveData<ItemEntity?>

    @Query("SELECT * FROM items")
    fun getAllItems(): LiveData<List<ItemEntity>>

    @Query("SELECT * FROM items WHERE status = :status")
    fun getItemsByStatus(status: String): LiveData<List<ItemEntity>>
}
