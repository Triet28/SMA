package com.DADN.smartplantcare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.DADN.smartplantcare.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view  = loginBinding.root
        setContentView(view)



        loginBinding.newHereRegister.setOnClickListener {
            val intent: Intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        loginBinding.loginButton.setOnClickListener {
            val userEmail: String = loginBinding.editTextTextEmailAddress.text.toString()
            val userPassword: String = loginBinding.editTextTextPassword.text.toString()

            login(userEmail, userPassword)
        }

    }

    fun login(userEmail: String, userPassword: String) {
        if (userEmail.isBlank() || userPassword.isBlank()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent: Intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, task.exception?.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}