package com.mycompany.sma.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycompany.sma.data.model.User
import com.mycompany.sma.ui.HomepageActivity
import com.mycompany.sma.ui.LoginActivity
import java.security.MessageDigest


object UserRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myRef: DatabaseReference = db.reference.child("Users")
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
    fun register(context: Context,
                 activity: Activity,
                 email: String,
                 password: String,
                 username: String,
                 name: String,
                 phoneNumber: String) {
        val hashedPassword = hashPassword(password)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    val userID = auth.currentUser?.uid
                    if (userID != null) {
                        val user = User(userID, username, email, hashedPassword, name, phoneNumber)
                        myRef.child(userID).setValue(user)
                            .addOnCompleteListener { saveTask ->
                                if(saveTask.isSuccessful){
                                    saveUserToDatabase(context, username, email, hashedPassword, name, phoneNumber, userID)
                                    Toast.makeText(context, "User Added Successfully!", Toast.LENGTH_SHORT).show()
                                    activity.startActivity(Intent(context, LoginActivity::class.java))
                                    activity.finish()
                                } else {
                                    Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveUserToDatabase(context: Context,
                                   username: String,
                                   email: String,
                                   hashedPassword: String,
                                   name: String,
                                   phoneNumber: String,
                                   userID: String) {
        val user = User(userID, username, email, hashedPassword, name, phoneNumber)
        myRef.child(userID).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "User Added Successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun login(context: Context,
              activity: Activity,
              email: String,
              password: String) {
        val loginTask = auth.signInWithEmailAndPassword(email, password)
        loginTask.addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                activity.startActivity(Intent(context, HomepageActivity::class.java))
                activity.finish()
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


