package com.vibenest.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.R
import com.vibenest.firebase.FirebaseManager
import com.vibenest.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val firebaseManager = FirebaseManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if user is already logged in
        if (firebaseManager.getCurrentUser() != null) {
            startMainActivity()
            return
        }

        setupTextWatchers()
        setupClickListeners()
    }

    private fun setupTextWatchers() {
        binding.emailInput.addTextChangedListener {
            binding.emailInputLayout.error = null
        }
        binding.passwordInput.addTextChangedListener {
            binding.passwordInputLayout.error = null
        }
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            if (validateInputs()) {
                performLogin()
            }
        }

        binding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.forgotPasswordButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailInputLayout.error = "Please enter a valid email"
                return@setOnClickListener
            }

            showLoading(true)
            firebaseManager.getFCMToken { token, error ->
                token?.let { fcmToken ->
                    // Send password reset email
                    firebaseManager.updateUserProfile(
                        mapOf("fcmToken" to fcmToken)
                    ) { success, updateError ->
                        showLoading(false)
                        if (success) {
                            Toast.makeText(
                                this,
                                "Password reset email sent",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                updateError ?: "Failed to send reset email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } ?: run {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        error ?: "Failed to send reset email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate Email
        val email = binding.emailInput.text.toString().trim()
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Invalid email format"
            isValid = false
        }

        // Validate Password
        val password = binding.passwordInput.text.toString()
        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Password is required"
            isValid = false
        }

        return isValid
    }

    private fun performLogin() {
        showLoading(true)

        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()

        try {
            firebaseManager.signIn(email, password) { success, error ->
                if (success) {
                    Log.d(TAG, "Login successful")
                    startMainActivity()
                } else {
                    Log.e(TAG, "Login failed: $error")
                    showLoading(false)
                    Toast.makeText(
                        this,
                        error ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during login", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            showLoading(false)
            Toast.makeText(
                this,
                "An error occurred during login",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, VibeNestMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.loginButton.isEnabled = !show
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
