package com.mycompany.sma

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

class PumpControl { // Not an activity class
    private var adafruitApiService: AdafruitApi

    init {
        adafruitApiService = RetrofitClient.instance.create(AdafruitApi::class.java)
    }

    fun sendFeedDataWrapper(value: Int) { // Wrapper function cause cannot call...
        sendFeedData(value)               //.. private function in main
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
}

