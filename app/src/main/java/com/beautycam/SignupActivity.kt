package com.beautycam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView

class SignupActivity : AppCompatActivity() {
    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var signupButton: MaterialButton
    private lateinit var loginLink: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        signupButton = findViewById(R.id.signupButton)
        loginLink = findViewById(R.id.loginLink)

        // Set click listeners
        signupButton.setOnClickListener {
            val name = nameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()

            when {
                name.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.msg_enter_name), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.msg_enter_email), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    Toast.makeText(this, getString(R.string.msg_enter_password), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    Toast.makeText(this, getString(R.string.msg_passwords_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // TODO: Implement user registration logic here

            Toast.makeText(this, getString(R.string.msg_registration_success), Toast.LENGTH_SHORT).show()
            // Navigate to MainActivity after successful registration
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        loginLink.setOnClickListener {
            // Navigate back to LoginActivity
            finish()
        }
    }
}
