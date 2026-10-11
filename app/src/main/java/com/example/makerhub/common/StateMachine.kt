package com.example.makerhub.common

import com.example.makerhub.domain.model.CheckoutStatus
import com.example.makerhub.domain.model.ItemStatus

fun isItemStatusValid(oldStatus: ItemStatus, newStatus: ItemStatus): Boolean {
    return when(oldStatus) {
        ItemStatus.AVAILABLE -> newStatus == ItemStatus.ON_LOAN || newStatus == ItemStatus.RETIRED
        ItemStatus.ON_LOAN -> newStatus == ItemStatus.AVAILABLE || newStatus == ItemStatus.LOST || newStatus == ItemStatus.RETIRED
        ItemStatus.LOST -> false
        ItemStatus.RETIRED -> false
    }
}

fun isCheckoutStatusValid(oldStatus: CheckoutStatus, newStatus: CheckoutStatus): Boolean {
    return when (oldStatus) {
        CheckoutStatus.ACTIVE -> newStatus == CheckoutStatus.RETURNED || newStatus == CheckoutStatus.LOST
        CheckoutStatus.RETURNED -> false
        CheckoutStatus.LOST -> false
    }
}