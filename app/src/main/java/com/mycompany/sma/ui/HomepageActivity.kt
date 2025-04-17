package com.mycompany.sma.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycompany.sma.data.repository.FeedResponseRepository
import com.mycompany.sma.data.repository.PlantRepository
import com.mycompany.sma.databinding.ActivityHomepageBinding


class HomepageActivity : AppCompatActivity() {
    private lateinit var homepageBinding: ActivityHomepageBinding
    private lateinit var database: DatabaseReference
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homepageBinding = ActivityHomepageBinding.inflate(layoutInflater)
        val view = homepageBinding.root
        setContentView(view)
        supportActionBar?.title = "Homepage"
        database = FirebaseDatabase.getInstance().getReference("Users/$userId/plants")
        PlantRepository.findPlantById(userId = userId, plantId = "plant1") {plant ->
            if (plant != null) {
                homepageBinding.plantNameTextView.text = plant.name
                homepageBinding.uploadPlant.text = null
                Glide.with(this)
                    .load(plant.imageURL)
                    .into(homepageBinding.plantImageView)
            } else {
                homepageBinding.plantNameTextView.text = "Plant not found"
            }
        }
        FeedResponseRepository.startUpdates()
    }
    override fun onDestroy() {
        super.onDestroy()
        FeedResponseRepository.stopUpdates() // Ngừng cập nhật khi Activity bị hủy
    }
}