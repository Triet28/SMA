package com.mycompany.sma.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.sma.data.repository.UserRepository
import com.mycompany.sma.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var loginbinding : ActivityLoginBinding
    private  val auth: FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginbinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginbinding.root
        setContentView(view)
        setupForgotPasswordLink()
        setupLoginButton()
        supportActionBar?.title = "Login"
    }
    private fun setupForgotPasswordLink() {
        val textView = loginbinding.forgotPasswordText
        val text = "Forgot password? Click here"
        val spannableString = SpannableString(text)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
                startActivity(intent)
            }
        }
        val startIndex = text.indexOf("Click here")
        val endIndex = startIndex + "Click here".length
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
    private fun setupLoginButton() {
        loginbinding.loginButton.setOnClickListener {
            val email = loginbinding.editEmail.text.toString()
            val password = loginbinding.editPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                UserRepository.login(context = this, activity = this, email = email, password = password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
