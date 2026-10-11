package com.example.makerhub.data.remote.response

import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("item_id") val itemId: Int? = null,
    @SerializedName("borrower_id") val borrowerId: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("checked_out_at") val checkedOutAt: String? = null,
    @SerializedName("due_at") val dueAt: String? = null,
    @SerializedName("returned_at") val returnedAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
