package com.example.makerhub.data.remote.response

import com.google.gson.annotations.SerializedName

data class ItemResponse(
    @SerializedName("id") val id: Any? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("category") val category: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
