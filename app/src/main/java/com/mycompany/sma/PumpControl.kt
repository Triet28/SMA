package com.mycompany.sma

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.sma.databinding.ActivityMainBinding
import android.os.Bundle
import com.mycompany.sma.entity.AdafruitResponse
import com.mycompany.sma.network.AdafruitApi
import com.mycompany.sma.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import com.mycompany.sma.databinding.ActivityRegisterBinding
import com.mycompany.sma.entity.FeedResponse
import android.os.Handler
import android.os.Looper


class PumpControl { // Not an activity class
    private var adafruitApiService: AdafruitApi

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 10000L // 10 second (5000ms)
    private val updateRunnable = object : Runnable {
        override fun run() {
            PumpLogic()
            getHumidityData()
            getTemperatureData()
            getSoilMoistureData()
            handler.postDelayed(this, updateInterval) // Repeat after 5 sec
        }
    }

    init {
        adafruitApiService = RetrofitClient.instance.create(AdafruitApi::class.java)
        updateRunnable.run()
    }

    fun PumpLogic() {
        val temperature: Float? = SensorDataSingleton.temperature // If null, exit function
        val humidity: Float? = SensorDataSingleton.humidity
        val moisture: Float? = SensorDataSingleton.moisture

        if (moisture == -1f) {
            sendFeedData(0)
            return

        } else if (moisture == 0f) {
            sendFeedData(1)
        }

        if (temperature == -1f) {
            sendFeedData(0)
            return

        } else if (temperature == 29.6f){
            sendFeedData(1)
        }

    }

    private fun sendFeedData(value: Int) {
        val call = adafruitApiService.sendDataToFeed("TQuanTum", "test-feed", value)
        call.enqueue(object : Callback<AdafruitResponse> {
            override fun onResponse(call: Call<AdafruitResponse>, response: Response<AdafruitResponse>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    Log.d("Adafruit", "Data Sent Successfully: ${responseData?.value}")
                } else {
                    Log.e("Adafruit", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AdafruitResponse>, t: Throwable) {
                Log.e("Adafruit", "Failure: ${t.message}")
            }
        })
    }

    private fun getHumidityData() {
        val call = adafruitApiService.getFeedData("TQuanTum", "humidity-feed")
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]

                        // Store latest data in singleton class
                        val humidity = latestFeed.value.toFloatOrNull()
                        if (humidity != null) {
                            SensorDataSingleton.getHumidData(humidity)
                        }

                        Log.d("AdafruitAPI", "Humidity: ${latestFeed.value}")

                    }
                } else {
                    SensorDataSingleton.getHumidData(-1f)
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }

    private fun getTemperatureData() {
        val call = adafruitApiService.getFeedData("TQuanTum", "sensor-temp")
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]

                        // Store latest value in singleton class
                        val temperature = latestFeed.value.toFloatOrNull()
                        if (temperature != null) {
                            SensorDataSingleton.getTempData(temperature)
                        }

                        Log.d("AdafruitAPI", "Temperature: ${latestFeed.value}")
                    } else {
                        SensorDataSingleton.getTempData(-1f)
                        Log.e("AdafruitAPI", "Empty temperature feed data")
                    }
                } else {
                    SensorDataSingleton.getTempData(-1f)
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }
    private fun getSoilMoistureData() {
        val call = adafruitApiService.getFeedData("TQuanTum", "soilmoisture-feed")
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy phần tử đầu tiên

                        // Store latest value in singleton class
                        val moisture = latestFeed.value.toFloatOrNull()
                        if (moisture != null) {
                            SensorDataSingleton.getMoistureData(moisture)
                        }

                        Log.d("AdafruitAPI", "Moisture: ${latestFeed.value}")
                    } else {
                        SensorDataSingleton.getMoistureData(-1f)
                        Log.e("AdafruitAPI", "Empty soil moisture feed data")
                    }
                } else {
                    SensorDataSingleton.getMoistureData(-1f)
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }

}

