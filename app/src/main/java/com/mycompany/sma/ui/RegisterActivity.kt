package com.mycompany.sma.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycompany.sma.data.repository.UserRepository
import com.mycompany.sma.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = db.reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        supportActionBar?.title = "Register"

        registerBinding.registerButton.setOnClickListener {
            val username = registerBinding.editUsername.text.toString().trim()
            val password = registerBinding.editPassword.text.toString().trim()
            val email = registerBinding.editEmail.text.toString().trim()
            val name = registerBinding.editName.text.toString().trim()
            val phoneNumber = registerBinding.editPhonenumber.text.toString().trim()
            val confirmPassword = registerBinding.editConfirmPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserRepository.register(
                context = this,
                activity = this,
                email = email,
                password = password,
                username = username,
                name = name,
                phoneNumber = phoneNumber
            )
        }
    }






}
