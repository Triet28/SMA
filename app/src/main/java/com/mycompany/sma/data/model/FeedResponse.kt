package com.mycompany.sma.data.model

data class FeedResponse(
    val feedresid: String,
    val createdAt: String,
    val value: String  // Dữ liệu gần nhất của cảm biến
)

