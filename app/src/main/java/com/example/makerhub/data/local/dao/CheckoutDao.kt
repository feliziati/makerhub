package com.example.makerhub.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.makerhub.common.DateUtils
import com.example.makerhub.common.isCheckoutStatusValid
import com.example.makerhub.data.local.entity.CheckoutEntity
import com.example.makerhub.data.remote.response.CheckoutResponse
import com.example.makerhub.data.remote.response.validateFields
import com.example.makerhub.domain.model.parseCheckoutStatus

@Dao
interface CheckoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checkout: CheckoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(checkouts: List<CheckoutEntity>): LongArray

    @Transaction
    suspend fun upsert(checkout: CheckoutEntity): Long {
        val newStatus = parseCheckoutStatus(checkout.status)
        val existing = getCheckoutById(checkout.id)
        return if (existing == null) {
            insert(checkout)
        } else {
            if (checkout.updatedAt > existing.updatedAt) {
                val oldStatus = parseCheckoutStatus(existing.status)
                if (!isCheckoutStatusValid(oldStatus, newStatus)) {
                    throw IllegalArgumentException("Cannot transition Checkout ${checkout.id} from $oldStatus to $newStatus")
                }
                update(checkout).toLong()
            } else {
                -1L
            }
        }
    }

    @Transaction
    suspend fun applyDelta(delta: CheckoutResponse): Long {
        if (delta.id == null) throw NullPointerException("id is null")

        val existing = getCheckoutById(delta.id) ?: return -1L

        var current = existing

        if (delta.updatedAt == null) throw NullPointerException("updated_at is null")

        if (DateUtils.parseTimestamp(delta.updatedAt) < existing.updatedAt) return -1L

        if (delta.itemId != null) {
            current = current.copy(itemId = delta.itemId)
        }

        if (delta.borrowerId != null) {
            current = current.copy(borrowerId = delta.borrowerId)
        }

        if (delta.status != null) {
            val oldStatus = parseCheckoutStatus(existing.status)
            val newStatus = parseCheckoutStatus(delta.status)
            if ((delta.status != existing.status) && isCheckoutStatusValid(oldStatus, newStatus)) {
                current = current.copy(status = delta.status)
            } else {
                throw IllegalArgumentException("Cannot transition Checkout ${existing.id} from $oldStatus to $newStatus")
            }
        }
        if (delta.checkedOutAt != null) {
            current = current.copy(checkedOutAt = DateUtils.parseTimestamp(delta.checkedOutAt))
        }

        if (delta.dueAt != null) {
            current = current.copy(dueAt = DateUtils.parseTimestamp(delta.dueAt))
        }

        if (delta.returnedAt != null ) {
            current = current.copy(returnedAt = DateUtils.parseTimestamp(delta.returnedAt))
        }

        current = current.copy(updatedAt = DateUtils.parseTimestamp(delta.updatedAt))

        return update(current).toLong()
    }


    @Transaction
    suspend fun upsertAll(checkouts: List<CheckoutEntity>): LongArray {
        return checkouts.map { upsert(it) }.toLongArray()
    }

    @Update
    suspend fun update(checkout: CheckoutEntity): Int

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

    @Query("SELECT * FROM checkouts WHERE itemId = :itemId")
    fun getCheckoutsByItem(itemId: Int): LiveData<List<CheckoutEntity>>

    @Query("SELECT * FROM checkouts WHERE itemId = :itemId")
    suspend fun getCheckoutsByItemSync(itemId: Int): List<CheckoutEntity>
}
