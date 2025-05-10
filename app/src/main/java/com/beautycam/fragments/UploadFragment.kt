package com.beautycam.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.beautycam.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class UploadFragment : Fragment() {
    private var videoUri: Uri? = null
    private lateinit var thumbnailView: ImageView
    private lateinit var titleInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var uploadButton: MaterialButton
    private lateinit var recordButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        thumbnailView = view.findViewById(R.id.thumbnailView)
        titleInput = view.findViewById(R.id.titleInput)
        descriptionInput = view.findViewById(R.id.descriptionInput)
        uploadButton = view.findViewById(R.id.uploadButton)
        recordButton = view.findViewById(R.id.recordButton)

        // Set click listeners
        uploadButton.setOnClickListener {
            if (validateInputs()) {
                uploadVideo()
            }
        }

        recordButton.setOnClickListener {
            // Navigate to VideoPreviewFragment for recording
            findNavController().navigate(R.id.action_global_videoPreviewFragment)
        }

        // Handle arguments (video URI) if coming from VideoPreviewFragment
        arguments?.getString("videoUri")?.let {
            videoUri = Uri.parse(it)
            // Load thumbnail from video
            loadVideoThumbnail()
        }
    }

    private fun validateInputs(): Boolean {
        if (videoUri == null) {
            Toast.makeText(context, getString(R.string.error_no_video), Toast.LENGTH_SHORT).show()
            return false
        }

        if (titleInput.text.isNullOrBlank()) {
            titleInput.error = getString(R.string.error_title_required)
            return false
        }

        if (descriptionInput.text.isNullOrBlank()) {
            descriptionInput.error = getString(R.string.error_description_required)
            return false
        }

        return true
    }

    private fun uploadVideo() {
        // TODO: Implement video upload logic
        Toast.makeText(context, getString(R.string.msg_upload_started), Toast.LENGTH_SHORT).show()
    }

    private fun loadVideoThumbnail() {
        // TODO: Load video thumbnail using media metadata retriever
        thumbnailView.setImageResource(R.drawable.ic_video_placeholder)
    }

    companion object {
        private const val TAG = "UploadFragment"
    }
}
