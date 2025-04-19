package com.mycompany.sma.data.model

data class Plant (
    val plantid: String,
    val name: String,
    val imageURL: String? = null,
    val humidity: FeedResponse? = null,
    val temperature: FeedResponse? = null,
    val soilMoisture: FeedResponse? = null
)