package com.example.makerhub.domain.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.makerhub.data.repository.LocalRepository
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.model.toItem
import javax.inject.Inject

class GetItemListUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun getAllItems(): LiveData<List<Item>> {
        return localRepository.getAllItems().map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toItem()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}
