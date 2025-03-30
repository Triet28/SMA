package com.mycompany.sma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycompany.sma.databinding.ActivityRegisterBinding
import com.mycompany.sma.entity.User
import java.security.MessageDigest
////// These are needed for clickable text
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
//////

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = db.reference.child("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)

        setupLoginText() // Call function of clickable text in register screen

        supportActionBar?.title = "Register"

        registerBinding.registerButton.setOnClickListener {
            val username = registerBinding.editUsername.text.toString().trim()
            val password = registerBinding.editPassword.text.toString().trim()
            val email = registerBinding.editEmail.text.toString().trim()
            val name = registerBinding.editName.text.toString().trim()
            val phoneNumber = registerBinding.editPhonenumber.text.toString().trim()
            val confirmPassword = registerBinding.editConfirmPassword.text.toString().trim()

            // Check input
            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check confirm password
            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hash password then create account
            val hashedPassword = hashPassword(password)
            createUser(email, password, username, name, phoneNumber)
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun createUser(email: String, password: String, username: String, name: String, phoneNumber: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid
                    if (userID != null) {
                        saveUserToDatabase(username, email, password, name, phoneNumber, userID)
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(username: String, email: String, hashedPassword: String, name: String, phoneNumber: String, userID: String) {
        val user = User(userID, username, email, hashedPassword, name, phoneNumber)
        myRef.child(userID).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "User Added Successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Makes "Let’s login!" clickable inside the "Already have an account? Let’s login!" text.
     */
    private fun setupLoginText() {
        val textView = registerBinding.tvSwitchToLogin  // Get the TextView
        val fullText = getString(R.string.switch_to_login)  // Load text from strings.xml
        val clickableText = "Let’s login!"  // The part that is clickable

        val spannableString = SpannableString(fullText)

        // Define what happens when the clickable part is tapped
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(widget.context, LoginActivity::class.java) // Direct to login screen
                widget.context.startActivity(intent)
            }
        }

        // Find the index of "Let’s login!" inside the full text
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length

        // Apply the clickable effect to only "Let’s login!"
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the formatted text with the clickable span
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()  // Enable link clicks
    }

}
