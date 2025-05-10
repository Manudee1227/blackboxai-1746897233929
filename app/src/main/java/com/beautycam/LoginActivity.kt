package com.beautycam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupLink: TextView
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize views
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)

        // Set click listeners
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_enter_credentials), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Implement authentication logic here

            Toast.makeText(this, getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show()
            // Navigate to MainActivity after successful login
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        forgotPasswordText.setOnClickListener {
            // TODO: Implement forgot password functionality
            Toast.makeText(this, getString(R.string.forgot_password), Toast.LENGTH_SHORT).show()
        }
    }
}
