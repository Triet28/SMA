package com.mycompany.sma.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mycompany.sma.data.model.Plant

object PlantRepository {
    fun findPlantById(
        userId: String,
        plantId: String,
        onResult: (Plant?) -> Unit
    ) {
        val plantsRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(userId)
            .child("plants")
        plantsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (plantSnapshot in snapshot.children) {
                        val plant = plantSnapshot.getValue(Plant::class.java)
                        if (plant != null && plant.id == plantId) {
                            onResult(plant)  //  Trả về Plant nếu tìm thấy
                            return
                        }
                    }
                    onResult(null)  // Không có cây nào
                } else {
                    onResult(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching plant data", error.toException())
                onResult(null)
            }
        })
    }
    fun createPlant(
        context: Context,
        userId: String,
        plant: Plant,
        onResult: (Boolean) -> Unit
    ) {
        val plantRef = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(userId)
            .child("plants")
            .child(plant.id)

        plantRef.setValue(plant)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Plant created successfully!", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    Toast.makeText(context, "Failed to create plant...Please try again", Toast.LENGTH_SHORT).show()
                    onResult(false)
                }
            }
    }
}