package com.example.makerhub.data.remote.response

import com.example.makerhub.data.remote.adapter.StringDeserializer
import com.example.makerhub.data.remote.adapter.StringToIntAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class ItemResponse(
    @JsonAdapter(StringToIntAdapter::class)
    @SerializedName("id") val id: Int? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category") val category: String? = null,
    @JsonAdapter(StringDeserializer::class)
    @SerializedName("status") val status: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

// if missing fields is empty, save the item, else report
fun ItemResponse.validateFields() {
    val missingFields = mutableListOf<String>()
    this.let {
        if (it.id == null) missingFields.add("id")
        if (it.name.isNullOrEmpty()) missingFields.add("name")
        if (it.category.isNullOrEmpty()) missingFields.add("category")
        if (it.status.isNullOrEmpty()) missingFields.add("status")
        if (it.updatedAt.isNullOrEmpty()) missingFields.add("updated_at")
    }
    require(missingFields.isEmpty()) { "Item $id has missing fields $missingFields" }
}
