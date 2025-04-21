package com.DADN.smartplantcare

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.DADN.smartplantcare.databinding.ActivityAddPlantsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddPlantsActivity : AppCompatActivity() {

    lateinit var addPlantBinding: ActivityAddPlantsBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef: DatabaseReference = database.reference.child("Plants")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPlantBinding = ActivityAddPlantsBinding.inflate(layoutInflater)
        val view = addPlantBinding.root
        setContentView(view)

        addPlantBinding.addButton.setOnClickListener {

                addPlantToDatabase()

        }

    }

    fun addPlantToDatabase() {
        val plantName = addPlantBinding.plantName.text.toString()
        val minSoilMoisture = addPlantBinding.minSoilMoisture.text.toString().toInt()
        val maxSoilMoisture = addPlantBinding.maxSoilMoisture.text.toString().toInt()
        val temperature = addPlantBinding.temperature.text.toString().toInt()
        val humidity = addPlantBinding.humidity.text.toString().toInt()

        val id: String = myRef.push().key.toString()

        val plant = Plants(id, plantName, minSoilMoisture, maxSoilMoisture, temperature, humidity)

        myRef.child(id).setValue(plant).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Plant added successfully", Toast.LENGTH_LONG).show()
                finish()
            }
            else {
                Toast.makeText(applicationContext, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }

    }

}