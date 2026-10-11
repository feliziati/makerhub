package com.example.makerhub.data.remote.response

import com.google.gson.annotations.SerializedName

data class LedgerDataResponse(
    @SerializedName("source") val source: String? = null,
    @SerializedName("exported_at") val exportedAt: String? = null,
    @SerializedName("export_timezone") val exportTimezone: String? = null,
    @SerializedName("legacy_date_format") val legacyDateFormat: String? = null,
    @SerializedName("items") val items: List<ItemResponse>? = null,
    @SerializedName("borrowers") val borrowers: List<BorrowerResponse>? = null,
    @SerializedName("checkouts") val checkouts: List<CheckoutResponse>? = null
)