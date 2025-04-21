package com.DADN.smartplantcare

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.DADN.smartplantcare.databinding.FragmentAutoModeBinding
import com.DADN.smartplantcare.FeedResponse
import com.DADN.smartplantcare.Plants
import com.DADN.smartplantcare.AdafruitApi
import com.DADN.smartplantcare.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.Menu

class AutoModeFragment : Fragment() {
    lateinit var autoModeFragmentBinding: FragmentAutoModeBinding
    lateinit var database: DatabaseReference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var adafruitApiService: AdafruitApi

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 5000L // 5 giây
    private val updateRunnable = object : Runnable {
        override fun run() {
            getHumidityData()
            getTemperatureData()
            getSoilMoistureData()
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        autoModeFragmentBinding = FragmentAutoModeBinding.inflate(inflater, container, false)
        adafruitApiService = RetrofitClient.instance.create(AdafruitApi::class.java)
        return autoModeFragmentBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val deleteAllItem = menu.findItem(R.id.deleteAll)
        deleteAllItem?.isVisible = false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow) // dùng icon back bạn muốn
        }


        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().getReference("Users/$userId/plants")
        findPlantById("plantId")
        updateRunnable.run()
    }

    private fun findPlantById(plantId: String) {
        val plantsRef = database
        plantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (plantSnapshot in snapshot.children) {
                        val plant = plantSnapshot.getValue(Plants::class.java)
                        if (plant != null && plant.plantId == plantId) {
                            autoModeFragmentBinding.plantName.text = plant.plantName
                            return
                        }
                    }
                    autoModeFragmentBinding.plantName.text = "Plant not found"
                } else {
                    autoModeFragmentBinding.plantName.text = "No plants available"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching plant data", error.toException())
            }
        })
    }

    private fun getHumidityData() {
        adafruitApiService.getFeedData("TQuanTum", "humidity-feed").enqueue(object : Callback<List<FeedResponse>> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val value = feedList[0].value
                        autoModeFragmentBinding.humidityValue.text = "$value %"
                    }
                } else {
                    Log.e("AdafruitAPI", "Humidity error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Humidity fail: ${t.message}")
            }
        })
    }

    private fun getTemperatureData() {
        adafruitApiService.getFeedData("TQuanTum", "sensor-temp").enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val value = feedList[0].value
                        autoModeFragmentBinding.temperatureValue.text = "$value °C"
                    }
                } else {
                    Log.e("AdafruitAPI", "Temp error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Temp fail: ${t.message}")
            }
        })
    }

    private fun getSoilMoistureData() {
        adafruitApiService.getFeedData("TQuanTum", "soilmoisture-feed").enqueue(object : Callback<List<FeedResponse>> {
            override fun onResponse(call: Call<List<FeedResponse>>, response: Response<List<FeedResponse>>) {
                if (response.isSuccessful) {
                    val feedList = response.body()
                    if (!feedList.isNullOrEmpty()) {
                        val value = feedList[0].value
                        autoModeFragmentBinding.soilMoistureValue.text = "$value %"
                    }
                } else {
                    Log.e("AdafruitAPI", "Soil moisture error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<FeedResponse>>, t: Throwable) {
                Log.e("AdafruitAPI", "Soil moisture fail: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().supportFragmentManager.popBackStack()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}
