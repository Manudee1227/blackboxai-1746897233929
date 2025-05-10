package com.beautycam.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beautycam.R
import com.beautycam.filters.video.VideoFilterManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.Slider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VideoPreviewFragment : Fragment() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var videoFilterManager: VideoFilterManager
    private var videoCapture: VideoCapture? = null
    private var isRecording = false
    private lateinit var outputDirectory: File

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_video_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize camera executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // Initialize video filter manager
        videoFilterManager = VideoFilterManager(requireContext())

        // Set up the output directory for recorded videos
        outputDirectory = File(
            requireContext().getExternalFilesDir(null),
            "BeautyCam/Videos"
        ).apply {
            mkdirs()
        }

        // Set up camera if permission is granted
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up UI controls
        setupUI(view)
    }

    private fun setupUI(view: View) {
        // Record button
        view.findViewById<MaterialButton>(R.id.recordButton).setOnClickListener {
            toggleRecording()
        }

        // Filter selection buttons
        view.findViewById<MaterialButton>(R.id.beautyFilterButton).setOnClickListener {
            videoFilterManager.setFilter(VideoFilterManager.FilterType.BEAUTY)
        }
        view.findViewById<MaterialButton>(R.id.vintageFilterButton).setOnClickListener {
            videoFilterManager.setFilter(VideoFilterManager.FilterType.VINTAGE)
        }
        view.findViewById<MaterialButton>(R.id.dramaticFilterButton).setOnClickListener {
            videoFilterManager.setFilter(VideoFilterManager.FilterType.DRAMATIC)
        }

        // Intensity slider
        view.findViewById<Slider>(R.id.filterIntensitySlider).addOnChangeListener { _, value, _ ->
            videoFilterManager.setIntensity(value)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Set up Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            view?.findViewById<PreviewView>(R.id.viewFinder)?.surfaceProvider
                        )
                    }

                // Set up VideoCapture
                videoCapture = VideoCapture.Builder()
                    .setVideoFrameRate(30)
                    .setTargetRotation(view?.display?.rotation ?: Surface.ROTATION_0)
                    .build()

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        videoCapture
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Use case binding failed", e)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Camera initialization failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleRecording() {
        val videoCapture = videoCapture ?: return

        if (isRecording) {
            // Stop recording
            videoCapture.stopRecording()
            isRecording = false
        } else {
            // Start recording
            val videoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".mp4"
            )

            val outputOptions = VideoCapture.OutputFileOptions.Builder(videoFile).build()

            videoCapture.startRecording(
                outputOptions,
                ContextCompat.getMainExecutor(requireContext()),
                object : VideoCapture.OnVideoSavedCallback {
                    override fun onVideoSaved(output: VideoCapture.OutputFileResults) {
                        val msg = getString(R.string.video_saved, output.savedUri.toString())
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        Log.d(TAG, msg)
                    }

                    override fun onError(exc: Int, message: String, cause: Throwable?) {
                        val msg = getString(R.string.video_capture_failed, message)
                        Log.e(TAG, msg, cause)
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
            )
            isRecording = true
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.permissions_not_granted),
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        videoFilterManager.release()
    }

    companion object {
        private const val TAG = "VideoPreviewFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        fun newInstance() = VideoPreviewFragment()
    }
}
