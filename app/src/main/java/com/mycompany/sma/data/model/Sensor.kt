package com.mycompany.sma.data.model

data class Sensor (
    val sensorid: String,
    val feedname: String,
    val userfeedname: String,
    val value: FeedResponse
)