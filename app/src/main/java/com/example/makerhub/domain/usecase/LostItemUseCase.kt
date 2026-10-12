package com.example.makerhub.domain.usecase

import android.util.Log
import com.example.makerhub.common.CheckoutStatus
import com.example.makerhub.common.IResult
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.Checkout
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.model.toEntity
import javax.inject.Inject

class LostItemUseCase @Inject constructor(val localRepository: LocalRepository) {
    private val TAG = LostItemUseCase::class.java.simpleName

    suspend operator fun invoke(item: Item, checkout: Checkout): IResult<Unit> {
        return try {
            val now = System.currentTimeMillis()
            val updatedItem = item.copy(
                status = ItemStatus.LOST,
                updatedAt = now
            )
            val updatedCheckout = checkout.copy(
                status = CheckoutStatus.LOST,
                updatedAt = now
            )
            localRepository.saveCheckout(updatedCheckout.toEntity())
            localRepository.saveItem(updatedItem.toEntity())
            IResult.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "error: $e")
            IResult.Error(e)
        }

    }
}