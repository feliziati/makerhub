package com.example.makerhub.presentation.ui.itemdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.makerhub.common.CheckoutStatus
import com.example.makerhub.common.IResult
import com.example.makerhub.di.AppComponent
import com.example.makerhub.domain.model.Borrower
import com.example.makerhub.domain.model.Checkout
import com.example.makerhub.domain.model.Item
import com.example.makerhub.domain.usecase.CheckoutItemUseCase
import com.example.makerhub.domain.usecase.GetItemDetailUseCase
import com.example.makerhub.domain.usecase.ReturnItemUseCase
import kotlinx.coroutines.launch

class ItemDetailViewModel(
    private val itemId: Int,
    private val getItemDetailUseCase: GetItemDetailUseCase,
    private val checkoutItemUseCase: CheckoutItemUseCase,
    private val returnItemUseCase: ReturnItemUseCase
) : ViewModel() {

    val itemLiveData: LiveData<Item?> = getItemDetailUseCase.getItem(itemId)
    val borrowersLiveData: LiveData<List<Borrower>> = getItemDetailUseCase.getBorrowers()

    val checkoutsLiveData: LiveData<List<Checkout>> = getItemDetailUseCase.getCheckouts(itemId)

    private val _actionResult = MutableLiveData<IResult<String>?>()
    val actionResult: LiveData<IResult<String>?> = _actionResult

    fun clearActionResult() {
        _actionResult.value = null
    }

    fun checkoutItem(borrower: Borrower) {
        viewModelScope.launch {
            itemLiveData.value?.let {
                when (val result = checkoutItemUseCase(it, borrower)) {
                    is IResult.Success -> _actionResult.value = IResult.Success("Item checked out successfully")
                    is IResult.Error -> _actionResult.value = IResult.Error(result.thr, errorMessage = result.thr.message  ?: "Failed to checkout item")
                }
            }
        }
    }

    fun returnItem() {
        viewModelScope.launch {
            val activeCheckout = checkoutsLiveData.value?.firstOrNull { it.status == CheckoutStatus.ACTIVE }
            if (activeCheckout != null) {
                when (val result = returnItemUseCase(activeCheckout)) {
                    is IResult.Success -> _actionResult.value = IResult.Success("Item returned successfully")
                    is IResult.Error -> _actionResult.value = IResult.Error(result.thr, errorMessage = result.thr.message ?: "Failed to return item")
                }
            }
        }
    }

    class Factory(
        private val itemId: Int,
        private val component: AppComponent
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ItemDetailViewModel(itemId, component.getItemDetailUseCase(), component.getCheckoutItemUseCase(), component.getReturnItemUseCase()) as T
        }
    }
}
