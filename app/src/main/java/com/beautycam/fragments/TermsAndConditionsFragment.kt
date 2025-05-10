package com.beautycam.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.beautycam.R

class TermsAndConditionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_terms_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = view.findViewById<ImageButton>(R.id.backButton)
        val termsText = view.findViewById<TextView>(R.id.termsText)
        val acceptButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.acceptButton)

        backButton.setOnClickListener {
            // TODO: Handle back navigation
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        acceptButton.setOnClickListener {
            // TODO: Handle acceptance action, e.g., close fragment or save acceptance
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        termsText.text = """
            Terms and Conditions

            1. User Responsibility
            You acknowledge and agree that you are solely responsible for all activities conducted through your account and any content you post or share using this application. The app and its owners disclaim any liability for actions taken by users.

            2. Compliance with Laws
            You agree to comply with all applicable laws and regulations, including but not limited to the Information Technology Act, 2000 of India, and relevant foreign laws governing online conduct and content.

            3. Prohibited Activities
            You shall not use the app for any unlawful, harmful, fraudulent, or malicious activities. This includes but is not limited to harassment, defamation, infringement of intellectual property rights, or distribution of illegal content.

            4. Limitation of Liability
            The app is provided "as is" without warranties of any kind. The app and its owners shall not be liable for any damages arising from your use of the app or any content posted by users.

            5. Governing Law and Jurisdiction
            These terms shall be governed by and construed in accordance with the laws of India. Any disputes arising shall be subject to the exclusive jurisdiction of the courts in India.

            6. Amendments
            The app reserves the right to modify these terms at any time. Continued use of the app constitutes acceptance of any changes.

            By using this app, you agree to these terms and conditions.
        """.trimIndent()
    }

    companion object {
        fun newInstance() = TermsAndConditionsFragment()
    }
}
