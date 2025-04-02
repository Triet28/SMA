package com.mycompany.sma

object SensorDataSingleton {
    var temperature: Float? = null
    var humidity: Float? = null
    var moisture: Float? = null

    fun getTempData(value: Float?) {
        temperature = value
    }

    fun getHumidData(value: Float?) {
        humidity = value
    }

    fun getMoistureData(value: Float?) {
        moisture = value
    }

}