package com.example.makerhub.presentation.ui.itemlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.makerhub.common.IResult
import com.example.makerhub.common.ItemStatus
import com.example.makerhub.di.AppComponent
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.usecase.GetItemListUseCase
import com.example.makerhub.domain.usecase.ImportDataUseCase

class ItemListViewModel(
    val importDataUseCase: ImportDataUseCase,
    val getItemListUseCase: GetItemListUseCase,
) : ViewModel() {

    private val _selectedStatusFilter = MutableLiveData<ItemStatus?>(null)
    val selectedStatusFilter: LiveData<ItemStatus?> = _selectedStatusFilter

    val allItems: LiveData<List<Item>> = getItemListUseCase.getAllItems()

    val filteredItems: LiveData<List<Item>> = MediatorLiveData<List<Item>>().apply {
        addSource(allItems) { items ->
            value = filterItems(items, _selectedStatusFilter.value)
        }
        addSource(_selectedStatusFilter) { filter ->
            value = filterItems(allItems.value ?: emptyList(), filter)
        }
    }

    fun setStatusFilter(status: ItemStatus?) {
        _selectedStatusFilter.value = status
    }

    suspend fun importData(isInit: Boolean): IResult<String> {
        return when (val result = importDataUseCase(isInit)) {
            is IResult.Success -> {
                IResult.Success(result.data)
            }

            is IResult.Error -> {
                IResult.Error(result.thr, "Something went wrong importing data")
            }
        }
    }

    private fun filterItems(items: List<Item>?, filter: ItemStatus?): List<Item> {
        val list = items ?: emptyList()
        return if (filter == null) {
            list
        } else {
            list.filter { item -> item.status == filter }
        }
    }

    class Factory(private val component: AppComponent) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItemListViewModel(component.getImportDataUseCase(), component.getGetItemsUseCase()) as T
        }
    }
}
