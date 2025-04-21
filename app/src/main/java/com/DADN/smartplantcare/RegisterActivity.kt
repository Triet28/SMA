package com.DADN.smartplantcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.DADN.smartplantcare.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var registerBinding: ActivityRegisterBinding
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        val view  = registerBinding.root
        setContentView(view)

        registerBinding.alreadyHaveAccLogin.setOnClickListener {
            val intent: Intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        registerBinding.registerButton.setOnClickListener {
            val userEmail = registerBinding.editTextTextEmailAddress.text.toString()
            val userPassword = registerBinding.editTextTextPassword.text.toString()

            if (userPassword.length < 6) {
                registerBinding.passwordError.text = "Your password must have at least 6 characters"
                registerBinding.passwordError.visibility = View.VISIBLE
            } else {
                registerBinding.passwordError.visibility = View.GONE
                registerUser(userEmail, userPassword)
            }
        }
    }

    fun registerUser(userEmail: String, userPassword: String) {
        auth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                finish()
            }else {
                Toast.makeText(this, task.exception?.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }
}