package com.example.makerhub.common

import android.util.Log

val TAG = "StateMachine"

enum class CheckoutStatus {
    ACTIVE, RETURNED, LOST
}

enum class ItemStatus {
    AVAILABLE, ON_LOAN, LOST, RETIRED
}

fun isItemStatusValid(oldStatus: ItemStatus, newStatus: ItemStatus): Boolean {
    Log.d(TAG, "isItemStatusValid(): oldStatus=$oldStatus, newStatus=$newStatus" )
    return oldStatus.name != newStatus.name && when(oldStatus) {
        ItemStatus.AVAILABLE -> newStatus == ItemStatus.ON_LOAN || newStatus == ItemStatus.RETIRED
        ItemStatus.ON_LOAN -> newStatus == ItemStatus.AVAILABLE || newStatus == ItemStatus.LOST || newStatus == ItemStatus.RETIRED
        ItemStatus.LOST -> false
        ItemStatus.RETIRED -> false
    }
}

fun isCheckoutStatusValid(oldStatus: CheckoutStatus, newStatus: CheckoutStatus): Boolean {
    Log.d(TAG, "isCheckoutStatusValid(): oldStatus=$oldStatus, newStatus=$newStatus" )
    return oldStatus.name != newStatus.name && when (oldStatus) {
        CheckoutStatus.ACTIVE -> newStatus == CheckoutStatus.RETURNED || newStatus == CheckoutStatus.LOST
        CheckoutStatus.RETURNED -> false
        CheckoutStatus.LOST -> false
    }
}

fun CheckoutStatus.toImpliedItemStatus(): ItemStatus {
    return when (this) {
        CheckoutStatus.ACTIVE -> ItemStatus.ON_LOAN
        CheckoutStatus.RETURNED -> ItemStatus.AVAILABLE
        CheckoutStatus.LOST -> ItemStatus.LOST
    }
}
