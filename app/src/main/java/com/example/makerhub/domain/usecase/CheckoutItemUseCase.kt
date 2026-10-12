package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.CheckoutStatus
import com.example.makerhub.common.IResult
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.Borrower
import com.example.makerhub.domain.model.Checkout
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.model.toEntity
import java.util.UUID
import javax.inject.Inject

class CheckoutItemUseCase @Inject constructor(val localRepository: LocalRepository) {

    private val TAG = CheckoutItemUseCase::class.java.simpleName
    companion object {
        const val DEFAULT_LOAN_PERIOD = 7 * 24 * 60 * 60 * 1000L
    }

    suspend operator fun invoke(item: Item, borrower: Borrower): IResult<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val updatedItem = item.copy(
                status = ItemStatus.ON_LOAN,
                updatedAt = now
            )
             // block item first to prevent checkout being created
            localRepository.saveItem(updatedItem.toEntity())
            val checkout = Checkout(
                id = "c-${UUID.randomUUID()}",
                item = item,
                borrower = borrower,
                status = CheckoutStatus.ACTIVE,
                checkedOutAt = now,
                dueAt = now + DEFAULT_LOAN_PERIOD,
                returnedAt = null,
                updatedAt = now
            )
            Log.i(TAG, "checkout=$checkout")
            Log.i(TAG, "prevItem=$item")
            Log.i(TAG, "updatedItem=$updatedItem")

            localRepository.saveCheckout(checkout.toEntity())
            IResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
            IResult.Error(e, e.message)
        }
    }
}