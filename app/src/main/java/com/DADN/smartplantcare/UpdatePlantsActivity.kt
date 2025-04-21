package com.DADN.smartplantcare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.DADN.smartplantcare.databinding.ActivityUpdatePlantsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UpdatePlantsActivity : AppCompatActivity() {

    lateinit var updatePlantsBinding: ActivityUpdatePlantsBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.reference.child("Plants")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updatePlantsBinding = ActivityUpdatePlantsBinding.inflate(layoutInflater)
        val view = updatePlantsBinding.root
        setContentView(view)

        getAndSetData()

        updatePlantsBinding.updateButton.setOnClickListener {
            updatePlant()
        }
    }

    fun getAndSetData() {
        val plantName = intent.getStringExtra("plantName")
        val minSoiMoisture = intent.getIntExtra("minSoilMoisture", 0).toString()
        val maxSoiMoisture = intent.getIntExtra("maxSoilMoisture", 0).toString()
        val temperature = intent.getIntExtra("temperature", 0).toString()
        val humidity = intent.getIntExtra("humidity", 0).toString()

        updatePlantsBinding.plantName.setText(plantName)
        updatePlantsBinding.minSoilMoisture.setText(minSoiMoisture)
        updatePlantsBinding.maxSoilMoisture.setText(maxSoiMoisture)
        updatePlantsBinding.temperature.setText(temperature)
        updatePlantsBinding.humidity.setText(humidity)
    }

    fun updatePlant() {
        val plantId = intent.getStringExtra("plantId").toString()
        val plantName = updatePlantsBinding.plantName.text.toString()
        val minSoiMoisture = updatePlantsBinding.minSoilMoisture.text.toString().toInt()
        val maxSoiMoisture = updatePlantsBinding.maxSoilMoisture.text.toString().toInt()
        val temperature = updatePlantsBinding.temperature.text.toString().toInt()
        val humidity = updatePlantsBinding.humidity.text.toString().toInt()

        val plantsMap = mutableMapOf<String, Any>()
        plantsMap["plantId"] = plantId
        plantsMap["plantName"] = plantName
        plantsMap["minSoilMoisture"] = minSoiMoisture
        plantsMap["maxSoilMoisture"] = maxSoiMoisture
        plantsMap["temperature"] = temperature
        plantsMap["humidity"] = humidity

        myRef.child(plantId).updateChildren(plantsMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Plant updated successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            else {
                Toast.makeText(applicationContext, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }

    }
}