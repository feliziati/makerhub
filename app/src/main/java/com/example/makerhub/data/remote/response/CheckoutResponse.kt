package com.example.makerhub.data.remote.response

import com.example.makerhub.data.remote.adapter.StringDeserializer
import com.example.makerhub.data.remote.adapter.StringToIntAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class CheckoutResponse(
    @JsonAdapter(StringDeserializer::class)
    @SerializedName("id") val id: String? = null,
    @JsonAdapter(StringToIntAdapter::class)
    @SerializedName("item_id") val itemId: Int? = null,
    @JsonAdapter(StringDeserializer::class)
    @SerializedName("borrower_id") val borrowerId: String? = null,
    @JsonAdapter(StringDeserializer::class)
    @SerializedName("status") val status: String? = null,
    @SerializedName("checked_out_at") val checkedOutAt: String? = null,
    @SerializedName("due_at") val dueAt: String? = null,
    @SerializedName("returned_at") val returnedAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

fun CheckoutResponse.validatePartialFields() {
    val missingFields = mutableListOf<String>()
    this.let {
        if (it.id.isNullOrEmpty()) missingFields.add("id")
        if (it.updatedAt.isNullOrEmpty()) missingFields.add("updated_at")
    }
    require(missingFields.isEmpty()) { "Checkout $id has missing fields $missingFields" }
}

fun CheckoutResponse.validateFields() {
    val missingFields = mutableListOf<String>()
    this.let {
        if (it.id.isNullOrEmpty()) missingFields.add("id")
        if (it.itemId == null) missingFields.add("item_id")
        if (it.borrowerId.isNullOrEmpty()) missingFields.add("borrower_id")
        if (it.status.isNullOrEmpty()) missingFields.add("status")
        if (it.checkedOutAt.isNullOrEmpty()) missingFields.add("checked_out_at")
        if (it.dueAt.isNullOrEmpty()) missingFields.add("returned_at")
        if (it.updatedAt.isNullOrEmpty()) missingFields.add("updated_at")
    }
    require(missingFields.isEmpty()) { "Checkout $id has missing fields $missingFields" }
}

