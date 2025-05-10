package com.vibenest.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vibenest.databinding.ActivitySignupBinding
import com.vibenest.firebase.FirebaseManager

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val firebaseManager = FirebaseManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
    }

    private fun setupViews() {
        try {
            binding.toolbar.setNavigationOnClickListener {
                finish()
            }

            binding.signupButton.setOnClickListener {
                if (validateInputs()) {
                    signup()
                }
            }

            binding.loginLink.setOnClickListener {
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up views", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (name.isEmpty()) {
            binding.nameInput.error = "Name is required"
            return false
        }

        if (email.isEmpty()) {
            binding.emailInput.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Invalid email format"
            return false
        }

        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInput.error = "Please confirm your password"
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun signup() {
        try {
            showLoading(true)

            val name = binding.nameInput.text.toString().trim()
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            val userData = mapOf(
                "name" to name,
                "email" to email,
                "bio" to "",
                "profileImage" to "",
                "postsCount" to 0,
                "followersCount" to 0,
                "followingCount" to 0,
                "isVerified" to false
            )

            firebaseManager.signUp(email, password, userData) { success, error ->
                showLoading(false)

                if (success) {
                    // Navigate to main activity
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                    finish()
                } else {
                    showError(error ?: "Failed to create account")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during signup", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            showLoading(false)
            showError("An unexpected error occurred")
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.signupButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    companion object {
        private const val TAG = "SignupActivity"
    }
}
