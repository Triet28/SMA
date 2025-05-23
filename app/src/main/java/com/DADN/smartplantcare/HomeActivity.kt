package com.DADN.smartplantcare

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.DADN.smartplantcare.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    lateinit var homeBinding: ActivityHomeBinding
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef = database.reference.child("Plants")
    val plantsList: ArrayList<Plants> = ArrayList()
    lateinit var plantsAdapter: plantsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        val view = homeBinding.root
        setContentView(view)

        plantsAdapter = plantsAdapter(this, plantsList) { plant ->
            openAutoModeFragment(plant.plantId)
        }

// Gắn layoutManager và adapter ngay lập tức
        homeBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        homeBinding.recyclerView.adapter = plantsAdapter


        homeBinding.addPlantsButton.setOnClickListener {
            val intent = Intent(this, AddPlantsActivity::class.java)
            startActivity(intent)
        }
        retriveDataFromDatabse()

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                homeBinding.fragmentContainer.visibility = View.GONE
                homeBinding.homeGroup.visibility = View.VISIBLE
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }

        homeBinding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Hiển thị lại danh sách cây
                    homeBinding.homeGroup.visibility = View.VISIBLE
                    homeBinding.fragmentContainer.visibility = View.GONE
                    true
                }

                R.id.nav_settings -> {
                    val settingsFragment = SettingsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .addToBackStack(null)
                        .commit()

                    homeBinding.homeGroup.visibility = View.GONE
                    homeBinding.fragmentContainer.visibility = View.VISIBLE
                    true
                }

                else -> false
            }
        }
    }



    fun retriveDataFromDatabse() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                plantsList.clear()

                for (eachPlant in snapshot.children) {
                    val plant = eachPlant.getValue(Plants::class.java)
                    if (plant != null) {
                        plantsList.add(plant)
                    }
                }

                plantsAdapter.notifyDataSetChanged()

                if (plantsList.isEmpty()) {
                    homeBinding.recyclerView.visibility = View.GONE
                    homeBinding.imageView2.visibility = View.VISIBLE
                    homeBinding.noPlantYet.visibility = View.VISIBLE
                } else {
                    homeBinding.recyclerView.visibility = View.VISIBLE
                    homeBinding.imageView2.visibility = View.GONE
                    homeBinding.noPlantYet.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Hiển thị lỗi nếu có
            }
        })
    }

    private fun openAutoModeFragment(plantId: String) {
        val fragment = AutoModeFragment().apply {
            arguments = Bundle().apply {
                putString("plantId", plantId)
            }
        }

        homeBinding.homeGroup.visibility = View.GONE

        homeBinding.fragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // có thể thay bằng ID của FrameLayout nếu bạn dùng layout fragment
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_all, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAll) {
            showDialogMessage()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialogMessage() {
        val dialogMessage = AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All Plants")
        dialogMessage.setMessage("Are you sure you want to delete all plants?")

        dialogMessage.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
        })

        dialogMessage.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            myRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    plantsAdapter.notifyDataSetChanged()
                    Toast.makeText(applicationContext, "All plants deleted", Toast.LENGTH_LONG)
                        .show()

                }
            }
        })

        dialogMessage.create().show()
    }

}