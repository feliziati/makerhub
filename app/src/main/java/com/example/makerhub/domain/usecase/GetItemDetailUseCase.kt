package com.example.makerhub.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.Borrower
import com.example.makerhub.domain.model.Checkout
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.model.toBorrower
import com.example.makerhub.domain.model.toCheckout
import com.example.makerhub.domain.model.toItem
import javax.inject.Inject

class GetItemDetailUseCase @Inject constructor(
    private val localRepository: LocalRepository,
) {

    fun getItem(itemId: Int): LiveData<Item?> =
        localRepository.getItemByIdLiveData(itemId).map {
            try {
                it?.toItem()
            } catch (e: Exception) {
                null
            }
        }

    fun getBorrowers(): LiveData<List<Borrower>> =
        localRepository.getAllBorrowers().map { entities ->
            entities.map {
                it.toBorrower()
            }
        }

    fun getCheckouts(itemId: Int): LiveData<List<Checkout>> {
        val checkoutsLiveData = localRepository.getCheckoutsByItem(itemId)
        val itemLiveData = localRepository.getItemByIdLiveData(itemId)
        val borrowersLiveData = localRepository.getAllBorrowers()

        val result = MediatorLiveData<List<Checkout>>()

        fun update() {
            val checkoutEntities = checkoutsLiveData.value ?: return
            if (checkoutEntities.isEmpty()) {
                result.value = emptyList()
                return
            }

            val itemEntity = itemLiveData.value ?: return
            val item = try {
                itemEntity.toItem()
            } catch (e: Exception) {
                return
            }

            val borrowerEntities = borrowersLiveData.value ?: return
            val borrowersMap = borrowerEntities.mapNotNull { entity ->
                try {
                    entity.toBorrower()
                } catch (e: Exception) {
                    null
                }
            }.associateBy { it.id }

            val checkouts = checkoutEntities.mapNotNull { checkoutEntity ->
                val borrower = borrowersMap[checkoutEntity.borrowerId] ?: return@mapNotNull null
                try {
                    checkoutEntity.toCheckout(item, borrower)
                } catch (e: Exception) {
                    null
                }
            }

            result.value = checkouts.sortedByDescending { it.updatedAt }
        }

        result.addSource(checkoutsLiveData) { update() }
        result.addSource(itemLiveData) { update() }
        result.addSource(borrowersLiveData) { update() }

        return result
    }

}
