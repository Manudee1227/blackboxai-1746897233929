package com.beautycam.filters.looks

import jp.co.cyberagent.android.gpuimage.filter.*
import com.beautycam.filters.advanced.*

object BeautyCamLooks {
    
    // Accentuate: Enhances details and colors
    fun createAccentuateLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // Enhance details with HDR
            add(GPUImageHDRFilter().apply {
                setIntensity(0.6f)
            })
            
            // Boost colors
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f,  // Red boost
                    0.0f, 1.1f, 0.0f, 0.0f,  // Green boost
                    0.0f, 0.0f, 1.1f, 0.0f,  // Blue boost
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            
            // Add contrast and sharpness
            add(GPUImageContrastFilter(1.2f))
            add(GPUImageSharpenFilter(0.5f))
        })
    }

    // Morning: Adds warm, golden tones
    fun createMorningLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // Warm color temperature
            add(GPUImageWhiteBalanceFilter(5500f, 0.3f))
            
            // Golden hour effect
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    1.3f, 0.0f, 0.0f, 0.0f,  // Strong red
                    0.0f, 1.1f, 0.0f, 0.0f,  // Slight green
                    0.0f, 0.0f, 0.8f, 0.0f,  // Reduced blue
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            
            // Soft glow
            add(GPUImageGlamourGlowFilter().apply {
                setIntensity(0.3f)
                setRadius(2.0f)
            })
            
            // Slight HDR for sky detail
            add(GPUImageHDRFilter().apply {
                setIntensity(0.3f)
            })
        })
    }

    // Faded Glow: Softens highlights for a dreamy look
    fun createFadedGlowLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // Dreamy glow effect
            add(GPUImageGlamourGlowFilter().apply {
                setIntensity(0.5f)
                setRadius(4.0f)
            })
            
            // Fade effect
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    0.9f, 0.0f, 0.0f, 0.1f,  // Red with lift
                    0.0f, 0.9f, 0.0f, 0.1f,  // Green with lift
                    0.0f, 0.0f, 0.9f, 0.1f,  // Blue with lift
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            
            // Reduced contrast for softness
            add(GPUImageContrastFilter(0.9f))
            
            // Slight warmth
            add(GPUImageWhiteBalanceFilter(5500f, 0.1f))
        })
    }

    // Fine Art: Muted, desaturated tones
    fun createFineArtLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // Desaturate
            add(GPUImageSaturationFilter(0.7f))
            
            // Muted contrast curve
            add(GPUImageCurvesFilter().apply {
                setRGBControlPoints(
                    arrayOf(
                        floatArrayOf(0f, 0.1f),
                        floatArrayOf(0.5f, 0.5f),
                        floatArrayOf(1f, 0.9f)
                    ),
                    arrayOf(
                        floatArrayOf(0f, 0.1f),
                        floatArrayOf(0.5f, 0.5f),
                        floatArrayOf(1f, 0.9f)
                    ),
                    arrayOf(
                        floatArrayOf(0f, 0.1f),
                        floatArrayOf(0.5f, 0.5f),
                        floatArrayOf(1f, 0.9f)
                    )
                )
            })
            
            // Subtle grain for texture
            add(GPUImageGrainFilter(0.2f))
            
            // Cool temperature
            add(GPUImageWhiteBalanceFilter(6500f, -0.1f))
        })
    }

    // Pop: Boosts color and structure
    fun createPopLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // Enhance structure with HDR
            add(GPUImageHDRFilter().apply {
                setIntensity(0.5f)
            })
            
            // Boost colors
            add(GPUImageSaturationFilter(1.3f))
            
            // Increase contrast
            add(GPUImageContrastFilter(1.3f))
            
            // Sharpen details
            add(GPUImageSharpenFilter(0.6f))
        })
    }

    // Grunge: Adds textured, gritty overlays
    fun createGrungeLook(): GPUImageFilterGroup {
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            // High contrast
            add(GPUImageContrastFilter(1.4f))
            
            // Strong grain
            add(GPUImageGrainFilter(0.4f))
            
            // Retro effects (scratches and light leaks)
            add(GPUImageRetroluxFilter().apply {
                setIntensity(0.5f)
                setGrainIntensity(0.3f)
                setLightLeakIntensity(0.2f)
            })
            
            // Desaturate slightly
            add(GPUImageSaturationFilter(0.8f))
        })
    }
}
