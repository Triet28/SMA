package com.mycompany.sma

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mycompany.sma.databinding.ActivityHomepageBinding
import com.mycompany.sma.entity.FeedResponse
import com.mycompany.sma.entity.Plant
import com.mycompany.sma.network.AdafruitApi
import com.mycompany.sma.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomepageActivity : AppCompatActivity() {
    private lateinit var homepageBinding: ActivityHomepageBinding
    private lateinit var database: DatabaseReference
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var adafruitApiService: AdafruitApi
    private val handler = Handler(Looper.getMainLooper()) // Handler chạy trên main thread
    private val updateInterval = 5000L // 5 second (5000ms)
    private val updateRunnable = object : Runnable {
        override fun run() {
            getHumidityData()
            getTemperatureData()
            getSoilMoistureData()
            handler.postDelayed(this, updateInterval) // Tiếp tục lặp lại sau 5 sec
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homepageBinding = ActivityHomepageBinding.inflate(layoutInflater)
        adafruitApiService = RetrofitClient.instance.create(AdafruitApi::class.java)
        val view = homepageBinding.root
        setContentView(view)
        supportActionBar?.title = "Homepage"
        database = FirebaseDatabase.getInstance().getReference("Users/$userId/plants")
        findPlantById("plantID_1")
        updateRunnable.run()
    }

    private fun findPlantById(plantId: String) {
        val plantsRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("plants")
        plantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseDebug", "Snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    for (plantSnapshot in snapshot.children) {
                        val plant = plantSnapshot.getValue(Plant::class.java)
                        if (plant != null && plant.id == plantId) {
                            // Nếu tìm thấy, hiển thị thông tin
                            homepageBinding.plantNameTextView.text = plant.name
                            homepageBinding.uploadPlant.text = null
                            Glide.with(this@HomepageActivity).load(plant.imageURL).into(homepageBinding.plantImageView)
                            return  // Dừng vòng lặp sau khi tìm thấy
                        }
                    }
                    // Không tìm thấy cây nào có id mong muốn
                    homepageBinding.plantNameTextView.text = "Plant not found"
                } else {
                    homepageBinding.plantNameTextView.text = "No plants available"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching plant data", error.toException())
            }
        })
    }

    private fun getHumidityData() {
        val call = adafruitApiService.getFeedData("TQuanTum", "humidity-feed")
        call.enqueue(object : Callback<List<FeedResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy dữ liệu đầu tiên

                        // Store latest data in singleton class
                        //val humidity = latestFeed.value.toFloatOrNull()
                        //if (humidity != null) {
                            //SensorDataSingleton.getHumidData(humidity)
                        //}

                        Log.d("AdafruitAPI", "Humidity: ${latestFeed.value}")
                        runOnUiThread {
                            homepageBinding.humidityValue.text = "${latestFeed.value} %"
                        }
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
        val call = adafruitApiService.getFeedData("TQuanTum", "sensor-temp")
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy phần tử đầu tiên

                        // Store latest value in singleton class
                        //val temperature = latestFeed.value.toFloatOrNull()
                        //if (temperature != null) {
                            //SensorDataSingleton.getTempData(temperature)
                        //}

                        Log.d("AdafruitAPI", "Temperature: ${latestFeed.value}")
                        homepageBinding.temperatureValue.text = "${latestFeed.value} °C"
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
    private fun getSoilMoistureData() {
        val call = adafruitApiService.getFeedData("TQuanTum", "soilmoisture-feed")
        call.enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val latestFeed = feedList[0]  // Lấy phần tử đầu tiên

                        // Store latest value in singleton class
                        //val moisture = latestFeed.value.toFloatOrNull()
                        //if (moisture != null) {
                            //SensorDataSingleton.getMoistureData(moisture)
                        //}

                        Log.d("AdafruitAPI", "Temperature: ${latestFeed.value}")
                        homepageBinding.soilMoistureValue.text = "${latestFeed.value} %"
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable) // Ngừng cập nhật khi Activity bị hủy
    }
}