package com.beautycam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.beautycam.LoginActivity
import com.beautycam.R
import com.beautycam.VibeNestMainActivity
import com.beautycam.fragments.TermsAndConditionsFragment
import com.beautycam.utils.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class SignupActivity : AppCompatActivity() {

    private lateinit var nameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var termsCheckbox: CheckBox
    private lateinit var termsText: TextView
    private lateinit var signupButton: MaterialButton
    private lateinit var loginLink: TextView
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        preferenceManager = PreferenceManager(this)
        initViews()
        setupListeners()
    }

    private fun initViews() {
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        termsCheckbox = findViewById(R.id.termsCheckbox)
        termsText = findViewById(R.id.termsText)
        signupButton = findViewById(R.id.signupButton)
        loginLink = findViewById(R.id.loginLink)
    }

    private fun setupListeners() {
        termsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            signupButton.isEnabled = isChecked
        }

        termsText.setOnClickListener {
            showTermsAndConditions()
        }

        signupButton.setOnClickListener {
            if (validateInputs()) {
                performSignup()
            }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun showTermsAndConditions() {
        val termsFragment = TermsAndConditionsFragment.newInstance()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            add(android.R.id.content, termsFragment)
            addToBackStack(null)
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (nameInput.text.isNullOrBlank()) {
            nameInput.error = "Name is required"
            isValid = false
        }

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
        } else if (passwordInput.text.toString().length < 6) {
            passwordInput.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPasswordInput.text.toString() != passwordInput.text.toString()) {
            confirmPasswordInput.error = "Passwords do not match"
            isValid = false
        }

        if (!termsCheckbox.isChecked) {
            MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Dark)
                .setTitle("Terms and Conditions")
                .setMessage("You must accept the Terms and Conditions to create an account")
                .setPositiveButton("View Terms") { _, _ -> showTermsAndConditions() }
                .setNegativeButton("Cancel", null)
                .show()
            isValid = false
        }

        return isValid
    }

    private fun performSignup() {
        // Store terms acceptance time
        preferenceManager.apply {
            setTermsAccepted(true)
            setTermsAcceptanceTimestamp(System.currentTimeMillis())
            
            // TODO: Store user credentials securely
            // For demo, we'll just store the email to simulate login state
            // In production, implement proper authentication
            putString("user_email", emailInput.text.toString())
            putString("user_name", nameInput.text.toString())
            putBoolean("is_logged_in", true)
        }

        // Proceed to main activity
        startActivity(Intent(this, VibeNestMainActivity::class.java))
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
