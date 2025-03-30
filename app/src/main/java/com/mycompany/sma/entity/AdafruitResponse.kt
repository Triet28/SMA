package com.mycompany.sma.entity

import com.google.gson.annotations.SerializedName

data class AdafruitResponse(
    @SerializedName("id") val id: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("value") val value: String
)