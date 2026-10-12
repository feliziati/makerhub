package com.example.makerhub.data.remote.response

import com.example.makerhub.data.remote.adapter.StringDeserializer
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName

data class BorrowerResponse(
    @JsonAdapter(StringDeserializer::class)
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("joined_at") val joinedAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

fun BorrowerResponse.validateFields() {
    val missingFields = mutableListOf<String>()
    this.let {
        if (it.id.isNullOrEmpty()) missingFields.add("id")
        if (it.name.isNullOrEmpty()) missingFields.add("name")
        if (it.email.isNullOrEmpty()) missingFields.add("email")
        if (it.joinedAt.isNullOrEmpty()) missingFields.add("joined_at")
    }
    require(missingFields.isEmpty()) { "Borrower $id has missing fields $missingFields" }
}
