package com.example.makerhub.data.remote.response

import com.google.gson.annotations.SerializedName

data class BorrowerResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("joined_at") val joinedAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)
