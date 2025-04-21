package com.DADN.smartplantcare

import com.google.gson.annotations.SerializedName

data class FeedResponse(
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("value") val value: String  // Dữ liệu gần nhất của cảm biến
)

