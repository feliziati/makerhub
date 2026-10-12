package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.CheckoutStatus
import com.example.makerhub.common.IResult
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.Checkout
import com.example.makerhub.domain.model.toEntity
import javax.inject.Inject

class ReturnItemUseCase @Inject constructor(val localRepository: LocalRepository) {

    private val TAG = ReturnItemUseCase::class.java.simpleName

    suspend operator fun invoke(checkout: Checkout): IResult<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val checkout = checkout.copy(
                id = checkout.id,
                item = checkout.item,
                borrower = checkout.borrower,
                status = CheckoutStatus.RETURNED,
                checkedOutAt = checkout.checkedOutAt,
                dueAt = checkout.dueAt,
                returnedAt = now,
                updatedAt = now
            )
            val previousItem = checkout.item
            val updatedItem = checkout.item.copy(
                status = ItemStatus.AVAILABLE,
                updatedAt = now
            )

            Log.i(TAG, "checkout=$checkout")
            Log.i(TAG, "prevItem=$previousItem")
            Log.i(TAG, "updatedItem=$updatedItem")

            localRepository.saveCheckout(checkout.toEntity())
            localRepository.saveItem(updatedItem.toEntity())
            IResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
            IResult.Error(e, "Could not return item. Reason: ${e.message}")
        }
    }
}