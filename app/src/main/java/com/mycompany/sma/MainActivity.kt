package com.mycompany.sma

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycompany.sma.databinding.ActivityMainBinding
import com.mycompany.sma.PumpControl

class MainActivity : AppCompatActivity() {
    lateinit var mainbinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainbinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainbinding.root
        setContentView(view)

        mainbinding.registerButton.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        mainbinding.loginButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        // Send 0 or 1 to feed to determine pump state
        val pumpState = PumpControl();
        pumpState.sendFeedDataWrapper(1); // Test value

    }
}


