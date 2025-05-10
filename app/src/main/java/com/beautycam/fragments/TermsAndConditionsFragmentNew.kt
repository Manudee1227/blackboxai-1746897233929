package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.beautycam.R
import com.beautycam.utils.PreferenceManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TermsAndConditionsFragment : Fragment() {

    private lateinit var preferenceManager: PreferenceManager
    private var onTermsAccepted: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireContext())

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val termsText = view.findViewById<TextView>(R.id.termsText)
        val acceptButton = view.findViewById<MaterialButton>(R.id.acceptButton)

        // Set terms text from resources
        termsText.text = getString(R.string.terms_and_conditions)

        // Show accept button only if terms haven't been accepted yet
        if (preferenceManager.isTermsAccepted()) {
            acceptButton.visibility = View.GONE
        }

        backButton.setOnClickListener {
            if (preferenceManager.isTermsAccepted()) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                showExitConfirmationDialog()
            }
        }

        acceptButton.setOnClickListener {
            showAcceptanceConfirmationDialog()
        }
    }

    private fun showAcceptanceConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Dark)
            .setTitle("Accept Terms and Conditions")
            .setMessage("By accepting, you agree to be bound by these Terms and Conditions and acknowledge that you have read and understood them.")
            .setPositiveButton("Accept") { _, _ ->
                acceptTerms()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showExitConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Dark)
            .setTitle("Exit Without Accepting?")
            .setMessage("You must accept the Terms and Conditions to use the app. Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ ->
                requireActivity().finish()
            }
            .setNegativeButton("Stay", null)
            .show()
    }

    private fun acceptTerms() {
        preferenceManager.apply {
            setTermsAccepted(true)
            setTermsAcceptanceTimestamp(System.currentTimeMillis())
        }
        onTermsAccepted?.invoke()
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    fun setOnTermsAcceptedListener(listener: () -> Unit) {
        onTermsAccepted = listener
    }

    companion object {
        fun newInstance() = TermsAndConditionsFragment()
    }
}
