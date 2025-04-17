package com.mycompany.sma.data.repository

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.mycompany.sma.BuildConfig
import com.mycompany.sma.data.model.FeedResponse
import com.mycompany.sma.data.model.RetrofitClient
import com.mycompany.sma.data.remote.AdafruitApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedResponseRepository {
    private val adafruitApi: AdafruitApi = RetrofitClient.instance.create(AdafruitApi::class.java)
    private const val updateInterval: Long = 10000L // 10seconds
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
    override fun run() {
                getHumidityData()
                getTemperatureData()
                getSoilMoistureData()
                handler.postDelayed(this, updateInterval)
        }
    }
    fun startUpdates() {
        updateRunnable.run()
    }

    fun stopUpdates() {
        handler.removeCallbacks(updateRunnable)
    }
    private fun getSoilMoistureData() {
        val call = adafruitApi.getFeedData("TQuanTum", "soilmoisture-feed", apiKey = BuildConfig.AIO_KEY)
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy phần tử đầu tiên
                        Log.d("AdafruitAPI", "Temperature: ${latestFeed.value}")
                    } else {
                        Log.e("AdafruitAPI", "Empty soil moisture feed data")
                    }
                } else {
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }
    private fun getHumidityData() {
        val call = adafruitApi.getFeedData("TQuanTum", "humidity-feed", apiKey = BuildConfig.AIO_KEY)
        call.enqueue(object : Callback<List<FeedResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestData = feedList[0]  // Lấy dữ liệu đầu tiên
                        Log.d("AdafruitAPI", "Humidity: ${latestData.value}")
                    }
                } else {
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }
    private fun getTemperatureData() {
        val call = adafruitApi.getFeedData("TQuanTum", "sensor-temp",apiKey = BuildConfig.AIO_KEY)
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy phần tử đầu tiên
                        Log.d("AdafruitAPI", "Temperature: ${latestFeed.value}")
                    } else {
                        Log.e("AdafruitAPI", "Empty temperature feed data")
                    }
                } else {
                    Log.e("AdafruitAPI", "Error: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Failed: ${t.message}")
            }
        })
    }
}