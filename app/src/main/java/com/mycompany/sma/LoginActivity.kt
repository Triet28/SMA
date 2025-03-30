package com.mycompany.sma

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
////// These are needed for clickable text
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
//////
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.sma.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var loginbinding : ActivityLoginBinding
    private  val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginbinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginbinding.root
        setContentView(view)


        loginbinding.loginButton.setOnClickListener {
            val email = loginbinding.editEmail.text.toString()
            val password = loginbinding.editPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        setupRegisterText() // Call function of clickable text in login screen

        supportActionBar?.title = "Login"
    }
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomepageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Makes "create an account!" clickable inside the "New here? Let’s create an account!" text.
     */
    private fun setupRegisterText() {
        val textView = findViewById<TextView>(R.id.tv_switch_to_register)  // Get TextView
        val fullText = getString(R.string.switch_to_register)  // Load text from strings.xml
        val clickableText = "create an account!"  // The part that is clickable

        val spannableString = SpannableString(fullText)

        // Define what happens when the clickable part is tapped
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(widget.context, RegisterActivity::class.java) // Direct to register screen
                widget.context.startActivity(intent)
            }
        }

        // Find the index of "create an account!" inside the full text
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length

        // Apply the clickable effect to only "create an account!"
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the formatted text with the clickable span
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()  // Enable link clicks
    }

}
