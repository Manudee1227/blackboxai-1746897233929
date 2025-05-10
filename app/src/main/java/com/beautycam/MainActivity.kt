package com.beautycam

import android.Manifest
import android.util.Log
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*
import com.beautycam.filters.*
import com.beautycam.filters.advanced.*
import com.beautycam.filters.looks.BeautyCamLooks
import com.beautycam.views.CurveView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var gpuImage: GPUImage
    private var imageCapture: ImageCapture? = null
    // Filters
    private var currentFilter: GPUImageFilter = GPUImageFilter()
    private var currentLook: GPUImageFilterGroup? = null
    private var currentTool: Tool = Tool.NONE
    
    // Beauty adjustments
    private var beautyModeEnabled: Boolean = false
    private var smoothnessLevel: Float = 0f
    private var skinToneLevel: Float = 0f
    private var faceSlimLevel: Float = 0f
    
    // Color adjustments
    private var brightnessLevel: Float = 0f
    private var contrastLevel: Float = 1f
    private var saturationLevel: Float = 1f
    private var sharpnessLevel: Float = 0f
    private var highlightsLevel: Float = 0f
    private var shadowsLevel: Float = 0f
    private var temperatureLevel: Float = 0f

    // Advanced filters
    private var hdrLevel: Float = 0f
    private var glamourGlowLevel: Float = 0f
    private var grainLevel: Float = 0f
    private var retroluxLevel: Float = 0f
    
    private enum class Tool {
        NONE, TUNE, CURVES, HDR, GRUNGE, RETROLUX, GLAMOUR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        try {
            setContentView(R.layout.activity_main)

            // Initialize camera and UI
            Log.d(TAG, "Initializing camera executor and GPU Image")
            cameraExecutor = Executors.newSingleThreadExecutor()
            gpuImage = GPUImage(this)
            
            Log.d(TAG, "Setting up UI")
            setupUI()

            // Check and request permissions
            if (allPermissionsGranted()) {
                Log.d(TAG, "All permissions granted, starting camera")
                startCamera()
            } else {
                Log.d(TAG, "Requesting camera permissions")
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
                )
            }

            // Restore previous filter state
            Log.d(TAG, "Restoring previous filter state")
            restoreFilterState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, getString(R.string.initialization_error), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupUI() {
        setupToolbar()
        setupQuickAccessTools()
        setupFilterControls()
        setupCaptureButton()
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.topAppBar).apply {
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setupQuickAccessTools() {
        // Beauty Mode Switch
        findViewById<SwitchMaterial>(R.id.beautyModeSwitch).apply {
            setOnCheckedChangeListener { _, isChecked ->
                beautyModeEnabled = isChecked
                updateFilters()
            }
        }

        // Flash Toggle
        findViewById<MaterialButton>(R.id.flashButton).apply {
            setOnClickListener {
                toggleFlash()
            }
        }

        // Camera Flip
        findViewById<MaterialButton>(R.id.flipCameraButton).apply {
            setOnClickListener {
                flipCamera()
            }
        }
    }

    private fun setupFilterControls() {
        setupToolsChipGroup()
        setupLooksChipGroup()
        setupTuneControls()
        setupCurveControls()
        setupAdvancedControls()
    }

    private fun setupToolsChipGroup() {
        findViewById<ChipGroup>(R.id.toolsChipGroup).apply {
            setOnCheckedChangeListener { _, checkedId ->
                currentTool = when (checkedId) {
                    R.id.toolTune -> Tool.TUNE
                    R.id.toolCurves -> Tool.CURVES
                    R.id.toolHDR -> Tool.HDR
                    R.id.toolGrunge -> Tool.GRUNGE
                    R.id.toolRetrolux -> Tool.RETROLUX
                    R.id.toolGlamour -> Tool.GLAMOUR
                    else -> Tool.NONE
                }
                updateControlsVisibility()
            }
        }
    }

    private fun setupLooksChipGroup() {
        findViewById<ChipGroup>(R.id.looksChipGroup).apply {
            setOnCheckedChangeListener { _, checkedId ->
                currentLook = when (checkedId) {
                    R.id.lookNone -> null
                    R.id.lookAccentuate -> BeautyCamLooks.createAccentuateLook()
                    R.id.lookMorning -> BeautyCamLooks.createMorningLook()
                    R.id.lookFadedGlow -> BeautyCamLooks.createFadedGlowLook()
                    R.id.lookFineArt -> BeautyCamLooks.createFineArtLook()
                    R.id.lookPop -> BeautyCamLooks.createPopLook()
                    else -> null
                }
                updateFilters()
            }
        }
    }

    private fun setupAdvancedControls() {
        // HDR Controls
        findViewById<Slider>(R.id.hdrIntensitySlider)?.addOnChangeListener { _, value, _ ->
            hdrLevel = value
            updateFilters()
        }

        // Grunge Controls
        findViewById<Slider>(R.id.grainIntensitySlider)?.addOnChangeListener { _, value, _ ->
            grainLevel = value
            updateFilters()
        }

        // Retrolux Controls
        findViewById<Slider>(R.id.retroluxIntensitySlider)?.addOnChangeListener { _, value, _ ->
            retroluxLevel = value
            updateFilters()
        }

        // Glamour Controls
        findViewById<Slider>(R.id.glamourIntensitySlider)?.addOnChangeListener { _, value, _ ->
            glamourGlowLevel = value
            updateFilters()
        }
    }

    private fun setupTuneControls() {
        // Beauty adjustments
        findViewById<Slider>(R.id.smoothnessSlider)?.addOnChangeListener { _, value, _ ->
            smoothnessLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.skinToneSlider)?.addOnChangeListener { _, value, _ ->
            skinToneLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.faceSlimSlider)?.addOnChangeListener { _, value, _ ->
            faceSlimLevel = value
            updateFilters()
        }

        // Color adjustments
        findViewById<Slider>(R.id.brightnessSlider)?.addOnChangeListener { _, value, _ ->
            brightnessLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.contrastSlider)?.addOnChangeListener { _, value, _ ->
            contrastLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.saturationSlider)?.addOnChangeListener { _, value, _ ->
            saturationLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.sharpnessSlider)?.addOnChangeListener { _, value, _ ->
            sharpnessLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.highlightsSlider)?.addOnChangeListener { _, value, _ ->
            highlightsLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.shadowsSlider)?.addOnChangeListener { _, value, _ ->
            shadowsLevel = value
            updateFilters()
        }
        findViewById<Slider>(R.id.temperatureSlider)?.addOnChangeListener { _, value, _ ->
            temperatureLevel = value
            updateFilters()
        }
    }

    private fun setupCurveControls() {
        val curveView = findViewById<CurveView>(R.id.curveView)
        curveView?.setCurveChangeListener { points ->
            // Update curves filter with new control points
            (currentFilter as? GPUImageCurvesFilter)?.let { filter ->
                filter.setRGBControlPoints(points, points, points)
                updateFilters()
            }
        }

        // Curve channel selection
        findViewById<ChipGroup>(R.id.channelChipGroup)?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.channelRGB -> curveView?.setChannel(CurveView.Channel.RGB)
                R.id.channelRed -> curveView?.setChannel(CurveView.Channel.RED)
                R.id.channelGreen -> curveView?.setChannel(CurveView.Channel.GREEN)
                R.id.channelBlue -> curveView?.setChannel(CurveView.Channel.BLUE)
            }
        }

        // Curve presets
        findViewById<ChipGroup>(R.id.curvePresetsChipGroup)?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.curveLinear -> curveView?.setPreset(CurveView.CurvePreset.LINEAR)
                R.id.curveMediumContrast -> curveView?.setPreset(CurveView.CurvePreset.MEDIUM_CONTRAST)
                R.id.curveHighContrast -> curveView?.setPreset(CurveView.CurvePreset.HIGH_CONTRAST)
                R.id.curveCrossPro -> curveView?.setPreset(CurveView.CurvePreset.CROSS_PROCESS)
                R.id.curveNegative -> curveView?.setPreset(CurveView.CurvePreset.NEGATIVE)
            }
        }

        // Reset button
        findViewById<MaterialButton>(R.id.resetCurveButton)?.setOnClickListener {
            curveView?.reset()
        }
    }

    private fun setupCaptureButton() {
        findViewById<FloatingActionButton>(R.id.captureButton).setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Configure Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(
                            findViewById<androidx.camera.view.PreviewView>(R.id.viewFinder).surfaceProvider
                        )
                    }

                // Configure ImageCapture
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                try {
                    // Unbind all previous use cases
                    cameraProvider.unbindAll()

                    // Bind new use cases
                    cameraProvider.bindToLifecycle(
                        this,
                        currentCameraSelector,
                        preview,
                        imageCapture
                    )

                    // Enable UI controls once camera is ready
                    setControlsEnabled(true)
                } catch (exc: Exception) {
                    handleCameraError(exc)
                }
            } catch (exc: Exception) {
                handleCameraError(exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        Log.d(TAG, "Taking photo")
        val imageCapture = imageCapture ?: run {
            Log.e(TAG, "ImageCapture not initialized")
            Toast.makeText(this, getString(R.string.camera_not_ready), Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Create output file
            val photoFile = File(
                outputDirectory,
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                    .format(System.currentTimeMillis()) + ".jpg"
            )
            Log.d(TAG, "Saving photo to: ${photoFile.absolutePath}")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            // Disable UI during capture
            setControlsEnabled(false)

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Log.d(TAG, "Photo captured successfully")
                        // Apply filters to captured image
                        applyFiltersToSavedImage(photoFile)
                    }

                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Error capturing photo", exc)
                        Toast.makeText(baseContext, getString(R.string.failed_save_photo), Toast.LENGTH_SHORT).show()
                        setControlsEnabled(true)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up photo capture", e)
            Toast.makeText(this, getString(R.string.camera_error), Toast.LENGTH_SHORT).show()
            setControlsEnabled(true)
        }
    }

    private fun applyFiltersToSavedImage(photoFile: File) {
        Log.d(TAG, "Applying filters to saved image: ${photoFile.name}")
        try {
            // Create a new GPUImage instance for processing the saved photo
            val photoGPUImage = GPUImage(this)
            photoGPUImage.setImage(photoFile)
            Log.d(TAG, "Image loaded into GPUImage")

            // Apply current filters
            val filterGroup = GPUImageFilterGroup()
            
            // Apply look filter if selected
            currentLook?.let { 
                Log.d(TAG, "Applying look filter")
                filterGroup.addFilter(it) 
            }
            
            // Apply tool-specific filters
            when (currentTool) {
                Tool.HDR -> if (hdrLevel > 0f) {
                    Log.d(TAG, "Applying HDR filter with level: $hdrLevel")
                    filterGroup.addFilter(GPUImageHDRFilter().apply { setIntensity(hdrLevel) })
                }
                Tool.GLAMOUR -> if (glamourGlowLevel > 0f) {
                    Log.d(TAG, "Applying Glamour filter with level: $glamourGlowLevel")
                    filterGroup.addFilter(GPUImageGlamourGlowFilter().apply {
                        setIntensity(glamourGlowLevel)
                        setRadius(3.0f)
                    })
                }
                Tool.GRUNGE -> if (grainLevel > 0f) {
                    Log.d(TAG, "Applying Grunge filter with level: $grainLevel")
                    filterGroup.addFilter(GPUImageGrainFilter(grainLevel))
                }
                Tool.RETROLUX -> if (retroluxLevel > 0f) {
                    Log.d(TAG, "Applying Retrolux filter with level: $retroluxLevel")
                    filterGroup.addFilter(GPUImageRetroluxFilter().apply {
                        setIntensity(retroluxLevel)
                        setGrainIntensity(0.3f)
                        setLightLeakIntensity(0.4f)
                    })
                }
                else -> Log.d(TAG, "No tool-specific filter needed")
            }

            // Apply beauty adjustments if enabled
            if (beautyModeEnabled) {
                Log.d(TAG, "Applying beauty adjustments")
                if (skinToneLevel > 0f) {
                    Log.d(TAG, "Applying skin tone filter with level: $skinToneLevel")
                    filterGroup.addFilter(GPUImageSkinToneFilter().apply {
                        setToneLevel(skinToneLevel)
                    })
                }
                // Add other beauty filters when implemented
            }

            // Apply color adjustments
            Log.d(TAG, "Applying color adjustments")
            filterGroup.addFilter(createColorAdjustmentGroup())

            // Set and process filters
            photoGPUImage.setFilter(filterGroup)
            Log.d(TAG, "All filters applied, saving processed image")
            
            // Save the processed image
            val outputFileName = "BeautyCam_" + photoFile.name
            photoGPUImage.saveToPictures(photoFile.absolutePath, outputFileName) { uri ->
                Log.d(TAG, "Image saved successfully: $uri")
                runOnUiThread {
                    Toast.makeText(baseContext, getString(R.string.photo_saved), Toast.LENGTH_SHORT).show()
                    setControlsEnabled(true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying filters to saved image", e)
            runOnUiThread {
                Toast.makeText(baseContext, getString(R.string.failed_save_photo), Toast.LENGTH_SHORT).show()
                setControlsEnabled(true)
            }
        }
    }

    private fun setControlsEnabled(enabled: Boolean) {
        Log.d(TAG, "Setting controls enabled state to: $enabled")
        try {
            val controls = mapOf(
                R.id.captureButton to "Capture Button",
                R.id.flashButton to "Flash Button",
                R.id.flipCameraButton to "Flip Camera Button",
                R.id.toolsChipGroup to "Tools Group",
                R.id.looksChipGroup to "Looks Group"
            )

            controls.forEach { (id, name) ->
                try {
                    findViewById<View>(id)?.apply {
                        isEnabled = enabled
                        Log.d(TAG, "$name enabled state set to: $enabled")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting enabled state for $name", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting controls enabled state", e)
        }
    }

    private fun applyFilter(filter: GPUImageFilter) {
        Log.d(TAG, "Applying new filter: ${filter.javaClass.simpleName}")
        try {
            currentFilter = filter
            updateFilters()
            Log.d(TAG, "Filter applied successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying filter", e)
            Toast.makeText(this, getString(R.string.filter_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createColorAdjustmentGroup(): GPUImageFilterGroup {
        Log.d(TAG, "Creating color adjustment group")
        try {
            val filters = ArrayList<GPUImageFilter>()
            
            // Basic adjustments
            if (brightnessLevel != 0f) {
                Log.d(TAG, "Adding brightness filter: $brightnessLevel")
                filters.add(GPUImageBrightnessFilter(brightnessLevel))
            }
            if (contrastLevel != 1f) {
                Log.d(TAG, "Adding contrast filter: $contrastLevel")
                filters.add(GPUImageContrastFilter(contrastLevel))
            }
            if (saturationLevel != 1f) {
                Log.d(TAG, "Adding saturation filter: $saturationLevel")
                filters.add(GPUImageSaturationFilter(saturationLevel))
            }
            if (sharpnessLevel > 0f) {
                Log.d(TAG, "Adding sharpness filter: ${sharpnessLevel * 4f}")
                filters.add(GPUImageSharpenFilter(sharpnessLevel * 4f))
            }

            // Advanced adjustments
            if (highlightsLevel != 0f || shadowsLevel != 0f) {
                Log.d(TAG, "Adding highlight/shadow filter - Highlights: $highlightsLevel, Shadows: $shadowsLevel")
                filters.add(GPUImageHighlightShadowFilter().apply {
                    setHighlights(1.0f + highlightsLevel)
                    setShadows(1.0f + shadowsLevel)
                })
            }
            if (temperatureLevel != 0f) {
                val temp = 5500f + (temperatureLevel * 1000f)
                Log.d(TAG, "Adding white balance filter - Temperature: $temp")
                filters.add(GPUImageWhiteBalanceFilter().apply {
                    setTemperature(temp)
                    setTint(0f)
                })
            }

            Log.d(TAG, "Created color adjustment group with ${filters.size} filters")
            return GPUImageFilterGroup(filters)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating color adjustment group", e)
            // Return empty filter group if there's an error
            return GPUImageFilterGroup()
        }
    }

    private fun updateControlsVisibility() {
        Log.d(TAG, "Updating controls visibility for tool: $currentTool")
        try {
            // Hide all control layouts first
            hideAllControls()

            // Show the relevant controls based on current tool
            when (currentTool) {
                Tool.TUNE -> {
                    Log.d(TAG, "Showing tune controls")
                    showControl(R.id.tuneControls)
                }
                Tool.CURVES -> {
                    Log.d(TAG, "Showing curve controls")
                    showControl(R.id.curveControls)
                }
                Tool.HDR -> {
                    Log.d(TAG, "Showing HDR controls")
                    showHDRControls()
                }
                Tool.GRUNGE -> {
                    Log.d(TAG, "Showing grunge controls")
                    showGrungeControls()
                }
                Tool.RETROLUX -> {
                    Log.d(TAG, "Showing retrolux controls")
                    showRetroluxControls()
                }
                Tool.GLAMOUR -> {
                    Log.d(TAG, "Showing glamour controls")
                    showGlamourControls()
                }
                Tool.NONE -> {
                    Log.d(TAG, "Showing default view")
                    showDefaultView()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating controls visibility", e)
        }
    }

    private fun hideAllControls() {
        Log.d(TAG, "Hiding all controls")
        val controlIds = listOf(
            R.id.tuneControls,
            R.id.curveControls,
            R.id.hdrControls,
            R.id.grungeControls,
            R.id.retroluxControls,
            R.id.glamourControls
        )
        controlIds.forEach { controlId ->
            try {
                findViewById<View>(controlId)?.visibility = View.GONE
            } catch (e: Exception) {
                Log.e(TAG, "Error hiding control $controlId", e)
            }
        }
    }

    private fun showControl(controlId: Int) {
        try {
            findViewById<View>(controlId)?.visibility = View.VISIBLE
            Log.d(TAG, "Showed control: $controlId")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing control $controlId", e)
        }
    }

    private fun showHDRControls() {
        Log.d(TAG, "Setting up HDR controls with level: $hdrLevel")
        showControl(R.id.hdrControls)
        findViewById<Slider>(R.id.hdrIntensitySlider)?.value = hdrLevel
    }

    private fun showGrungeControls() {
        Log.d(TAG, "Setting up Grunge controls with level: $grainLevel")
        showControl(R.id.grungeControls)
        findViewById<Slider>(R.id.grainIntensitySlider)?.value = grainLevel
    }

    private fun showRetroluxControls() {
        Log.d(TAG, "Setting up Retrolux controls with level: $retroluxLevel")
        showControl(R.id.retroluxControls)
        findViewById<Slider>(R.id.retroluxIntensitySlider)?.value = retroluxLevel
    }

    private fun showGlamourControls() {
        Log.d(TAG, "Setting up Glamour controls with level: $glamourGlowLevel")
        showControl(R.id.glamourControls)
        findViewById<Slider>(R.id.glamourIntensitySlider)?.value = glamourGlowLevel
    }

    private fun showDefaultView() {
        Log.d(TAG, "Showing default filter series view")
        showControl(R.id.filterSeriesControls)
    }

    private fun resetAllAdjustments() {
        Log.d(TAG, "Resetting all adjustments")
        try {
            // Reset beauty adjustments
            Log.d(TAG, "Resetting beauty adjustments")
            beautyModeEnabled = false
            smoothnessLevel = 0f
            skinToneLevel = 0f
            faceSlimLevel = 0f

            // Reset color adjustments
            Log.d(TAG, "Resetting color adjustments")
            brightnessLevel = 0f
            contrastLevel = 1f
            saturationLevel = 1f
            sharpnessLevel = 0f
            highlightsLevel = 0f
            shadowsLevel = 0f
            temperatureLevel = 0f

            // Reset advanced filters
            Log.d(TAG, "Resetting advanced filters")
            hdrLevel = 0f
            glamourGlowLevel = 0f
            grainLevel = 0f
            retroluxLevel = 0f

            // Reset UI controls
            Log.d(TAG, "Resetting UI controls")
            findViewById<SwitchMaterial>(R.id.beautyModeSwitch)?.isChecked = false
            findViewById<ChipGroup>(R.id.toolsChipGroup)?.clearCheck()
            findViewById<ChipGroup>(R.id.looksChipGroup)?.clearCheck()
            
            // Reset current states
            currentTool = Tool.NONE
            currentLook = null
            currentFilter = GPUImageFilter()

            // Update UI
            updateControlsVisibility()
            updateFilters()
            
            Log.d(TAG, "All adjustments reset successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error resetting adjustments", e)
            Toast.makeText(this, getString(R.string.reset_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFilters() {
        Log.d(TAG, "Updating filters - Tool: $currentTool, BeautyMode: $beautyModeEnabled")
        
        try {
            val filterGroup = GPUImageFilterGroup()
            
            // Add current look if selected
            currentLook?.let { 
                Log.d(TAG, "Applying look filter")
                filterGroup.addFilter(it) 
            }
            
            // Add tool-specific filters
            when (currentTool) {
                Tool.HDR -> {
                    if (hdrLevel > 0f) {
                        Log.d(TAG, "Applying HDR filter with level: $hdrLevel")
                        filterGroup.addFilter(GPUImageHDRFilter().apply {
                            setIntensity(hdrLevel)
                        })
                    }
                }
                Tool.GLAMOUR -> {
                    if (glamourGlowLevel > 0f) {
                        Log.d(TAG, "Applying Glamour filter with level: $glamourGlowLevel")
                        filterGroup.addFilter(GPUImageGlamourGlowFilter().apply {
                            setIntensity(glamourGlowLevel)
                            setRadius(3.0f)
                        })
                    }
                }
                Tool.GRUNGE -> {
                    if (grainLevel > 0f) {
                        Log.d(TAG, "Applying Grunge filter with level: $grainLevel")
                        filterGroup.addFilter(GPUImageGrainFilter(grainLevel))
                    }
                }
                Tool.RETROLUX -> {
                    if (retroluxLevel > 0f) {
                        Log.d(TAG, "Applying Retrolux filter with level: $retroluxLevel")
                        filterGroup.addFilter(GPUImageRetroluxFilter().apply {
                            setIntensity(retroluxLevel)
                            setGrainIntensity(0.3f)
                            setLightLeakIntensity(0.4f)
                        })
                    }
                }
                else -> Log.d(TAG, "No specific tool filter needed")
            }
            
            // Add beauty adjustments if enabled
            if (beautyModeEnabled) {
                Log.d(TAG, "Applying beauty adjustments - Skin: $skinToneLevel, Smooth: $smoothnessLevel")
                if (skinToneLevel > 0f) {
                    filterGroup.addFilter(GPUImageSkinToneFilter().apply {
                        setToneLevel(skinToneLevel)
                    })
                }
                if (smoothnessLevel > 0f) {
                    // Add smoothness filter when implemented
                    Log.d(TAG, "Smoothness filter not yet implemented")
                }
                if (faceSlimLevel > 0f) {
                    // Add face slim filter when implemented
                    Log.d(TAG, "Face slim filter not yet implemented")
                }
            }
            
            // Add color adjustments
            filterGroup.addFilter(createColorAdjustmentGroup())
            
            // Apply all filters
            gpuImage.setFilter(filterGroup)
            Log.d(TAG, "Successfully applied all filters")
        } catch (e: Exception) {
            Log.e(TAG, "Error applying filters", e)
            Toast.makeText(this, getString(R.string.filter_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionsGranted(): Boolean {
        Log.d(TAG, "Checking camera permissions")
        return REQUIRED_PERMISSIONS.all { permission ->
            val isGranted = ContextCompat.checkSelfPermission(
                baseContext, 
                permission
            ) == PackageManager.PERMISSION_GRANTED
            
            Log.d(TAG, "Permission $permission granted: $isGranted")
            isGranted
        }.also { allGranted ->
            Log.d(TAG, "All permissions granted: $allGranted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (allPermissionsGranted()) {
                    startCamera()
                } else {
                    handlePermissionDenied()
                }
            }
        }
    }

    private fun handlePermissionDenied() {
        // Show a dialog explaining why we need the permission
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.camera_permission_rationale))
            .setPositiveButton(getString(R.string.settings)) { _, _ ->
                // Open app settings
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", packageName, null)
                })
                finish()
            }
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    companion object {
        private const val TAG = "BeautyCam"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        
        // Shared Preferences Keys
        private const val PREFS_NAME = "BeautyCamPrefs"
        private const val KEY_BEAUTY_MODE = "beauty_mode"
        private const val KEY_SMOOTHNESS = "smoothness"
        private const val KEY_SKIN_TONE = "skin_tone"
        private const val KEY_FACE_SLIM = "face_slim"
        private const val KEY_BRIGHTNESS = "brightness"
        private const val KEY_CONTRAST = "contrast"
        private const val KEY_SATURATION = "saturation"
        private const val KEY_SHARPNESS = "sharpness"
        private const val KEY_HIGHLIGHTS = "highlights"
        private const val KEY_SHADOWS = "shadows"
        private const val KEY_TEMPERATURE = "temperature"
        
        private val outputDirectory: File by lazy {
            val mediaDir = File("/storage/emulated/0/DCIM/BeautyCam")
            if (!mediaDir.exists()) {
                mediaDir.mkdirs()
            }
            mediaDir
        }
    }

    private fun handleCameraError(exc: Exception) {
        Log.e(TAG, "Camera error: ${exc.message}", exc)
        
        val errorMessage = when (exc) {
            is CameraUnavailableException -> getString(R.string.camera_unavailable)
            is SecurityException -> getString(R.string.camera_permission_denied)
            else -> getString(R.string.camera_error)
        }
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error))
            .setMessage(errorMessage)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                Log.d(TAG, "Retrying camera start")
                startCamera()
            }
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                Log.d(TAG, "User chose to exit after camera error")
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onBackPressed() {
        when {
            currentTool != Tool.NONE -> {
                // If a tool is active, clear it and return to default view
                findViewById<ChipGroup>(R.id.toolsChipGroup)?.clearCheck()
                currentTool = Tool.NONE
                updateControlsVisibility()
            }
            else -> {
                // If we're at the default view, exit the app
                super.onBackPressed()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        try {
            // Restore filter state when activity resumes
            Log.d(TAG, "Restoring filter state")
            restoreFilterState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause called")
        try {
            // Save filter state when activity pauses
            Log.d(TAG, "Saving filter state")
            saveFilterState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "onStop called")
        try {
            // Save filter state when activity stops
            Log.d(TAG, "Saving filter state")
            saveFilterState()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStop", e)
        }
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called")
        try {
            // Save filter state before destroying
            Log.d(TAG, "Saving final filter state")
            saveFilterState()
            
            // Clean up camera resources
            Log.d(TAG, "Cleaning up camera resources")
            imageCapture = null
            
            // Clean up GPU Image resources
            Log.d(TAG, "Recycling GPU Image")
            gpuImage.recycle()
            
            // Shutdown executor
            Log.d(TAG, "Shutting down camera executor")
            cameraExecutor.shutdown()
            
            Log.d(TAG, "Cleanup completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup in onDestroy", e)
        } finally {
            super.onDestroy()
        }
    }

    private fun saveFilterState() {
        Log.d(TAG, "Saving filter state")
        try {
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().apply {
                putBoolean(KEY_BEAUTY_MODE, beautyModeEnabled)
                putFloat(KEY_SMOOTHNESS, smoothnessLevel)
                putFloat(KEY_SKIN_TONE, skinToneLevel)
                putFloat(KEY_FACE_SLIM, faceSlimLevel)
                putFloat(KEY_BRIGHTNESS, brightnessLevel)
                putFloat(KEY_CONTRAST, contrastLevel)
                putFloat(KEY_SATURATION, saturationLevel)
                putFloat(KEY_SHARPNESS, sharpnessLevel)
                putFloat(KEY_HIGHLIGHTS, highlightsLevel)
                putFloat(KEY_SHADOWS, shadowsLevel)
                putFloat(KEY_TEMPERATURE, temperatureLevel)
            }.apply()
            Log.d(TAG, "Filter state saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving filter state", e)
        }
    }

    private fun restoreFilterState() {
        Log.d(TAG, "Restoring filter state")
        try {
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            beautyModeEnabled = prefs.getBoolean(KEY_BEAUTY_MODE, false)
            smoothnessLevel = prefs.getFloat(KEY_SMOOTHNESS, 0f)
            skinToneLevel = prefs.getFloat(KEY_SKIN_TONE, 0f)
            faceSlimLevel = prefs.getFloat(KEY_FACE_SLIM, 0f)
            brightnessLevel = prefs.getFloat(KEY_BRIGHTNESS, 0f)
            contrastLevel = prefs.getFloat(KEY_CONTRAST, 1f)
            saturationLevel = prefs.getFloat(KEY_SATURATION, 1f)
            sharpnessLevel = prefs.getFloat(KEY_SHARPNESS, 0f)
            highlightsLevel = prefs.getFloat(KEY_HIGHLIGHTS, 0f)
            shadowsLevel = prefs.getFloat(KEY_SHADOWS, 0f)
            temperatureLevel = prefs.getFloat(KEY_TEMPERATURE, 0f)

            Log.d(TAG, "Restored values - Beauty: $beautyModeEnabled, " +
                      "Skin: $skinToneLevel, Brightness: $brightnessLevel")

            // Update UI to reflect restored values
            findViewById<SwitchMaterial>(R.id.beautyModeSwitch)?.isChecked = beautyModeEnabled
            updateFilters()
            Log.d(TAG, "Filter state restored successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring filter state", e)
            // Reset to defaults if restore fails
            resetAllAdjustments()
        }
    }

    private var isFlashEnabled = false
    private var currentCameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private fun toggleFlash() {
        Log.d(TAG, "Toggling flash mode")
        try {
            isFlashEnabled = !isFlashEnabled
            imageCapture?.flashMode = when (isFlashEnabled) {
                true -> {
                    Log.d(TAG, "Enabling flash")
                    ImageCapture.FLASH_MODE_ON
                }
                false -> {
                    Log.d(TAG, "Disabling flash")
                    ImageCapture.FLASH_MODE_OFF
                }
            }
            findViewById<MaterialButton>(R.id.flashButton).apply {
                setIconTintResource(if (isFlashEnabled) R.color.yellow else R.color.white)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling flash", e)
            Toast.makeText(this, getString(R.string.flash_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun flipCamera() {
        Log.d(TAG, "Flipping camera")
        try {
            setControlsEnabled(false) // Disable controls during camera switch
            currentCameraSelector = when (currentCameraSelector) {
                CameraSelector.DEFAULT_FRONT_CAMERA -> {
                    Log.d(TAG, "Switching to back camera")
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                else -> {
                    Log.d(TAG, "Switching to front camera")
                    CameraSelector.DEFAULT_FRONT_CAMERA
                }
            }
            startCamera() // Restart camera with new selector
        } catch (e: Exception) {
            Log.e(TAG, "Error flipping camera", e)
            Toast.makeText(this, getString(R.string.camera_switch_error), Toast.LENGTH_SHORT).show()
            setControlsEnabled(true)
        }
    }
}
