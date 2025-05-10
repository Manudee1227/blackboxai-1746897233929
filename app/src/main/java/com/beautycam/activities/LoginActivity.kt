package com.beautycam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.beautycam.R
import com.beautycam.VibeNestMainActivity
import com.beautycam.utils.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupLink: TextView
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        preferenceManager = PreferenceManager(this)
        
        // If user is already logged in, go directly to main activity
        if (preferenceManager.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, VibeNestMainActivity::class.java))
            finish()
            return
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        signupLink = findViewById(R.id.signupLink)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        signupLink.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (emailInput.text.isNullOrBlank()) {
            emailInput.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
            emailInput.error = "Invalid email format"
            isValid = false
        }

        if (passwordInput.text.isNullOrBlank()) {
            passwordInput.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    private fun performLogin() {
        // TODO: Implement actual login logic here
        // For demo, we'll just check if the email exists in preferences
        val savedEmail = preferenceManager.getString("user_email", "")
        
        if (emailInput.text.toString() == savedEmail) {
            preferenceManager.putBoolean("is_logged_in", true)
            startActivity(Intent(this, VibeNestMainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        } else {
            emailInput.error = "Account not found"
        }
    }
}
