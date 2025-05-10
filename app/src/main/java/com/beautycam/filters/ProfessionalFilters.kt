package com.beautycam.filters

import jp.co.cyberagent.android.gpuimage.filter.*
import android.graphics.Color
import kotlin.math.max
import kotlin.math.min

object ProfessionalFilters {
    
    // A-Series (Analog) Filters
    fun createA1Filter(): GPUImageFilterGroup {
        // Vintage film vibes with warm, muted tones
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageRGBFilter().apply {
                setRed(1.1f)    // Warm red tint
                setGreen(0.9f)  // Slightly reduced green
                setBlue(0.8f)   // Reduced blue for warmth
            })
            add(GPUImageSaturationFilter(0.8f))  // Slightly muted
            add(GPUImageContrastFilter(1.1f))    // Subtle contrast
            add(GPUImageVignetteFilter().apply {
                setVignetteStart(0.8f)
                setVignetteEnd(1.2f)
            })
            add(GPUImageGrainFilter(0.2f))       // Film grain effect
        })
    }

    fun createA2Filter(): GPUImageFilterGroup {
        // Warm tones with soft highlights
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f,  // Red channel boost
                    0.0f, 1.1f, 0.0f, 0.0f,  // Slight green boost
                    0.0f, 0.0f, 0.9f, 0.0f,  // Reduced blue
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            add(GPUImageHighlightShadowFilter(1.2f, 0.8f))  // Soften highlights
            add(GPUImageGrainFilter(0.15f))                 // Subtle grain
        })
    }

    fun createA3Filter(): GPUImageFilterGroup {
        // Muted tones with vintage feel
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageSaturationFilter(0.7f))     // More muted colors
            add(GPUImageSepiaToneFilter(0.3f))      // Slight sepia tint
            add(GPUImageHighlightShadowFilter(1.1f, 0.9f))
            add(GPUImageGrainFilter(0.25f))         // Noticeable grain
        })
    }

    // HB-Series (Hypebeast) Filters
    fun createHB1Filter(): GPUImageFilterGroup {
        // High contrast, cool tones
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageContrastFilter(1.4f))       // Strong contrast
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    0.9f, 0.0f, 0.0f, 0.0f,   // Reduced red
                    0.0f, 1.0f, 0.1f, 0.0f,   // Slight blue tint in green
                    0.0f, 0.0f, 1.2f, 0.0f,   // Boosted blue
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            add(GPUImageVignetteFilter().apply {
                setVignetteStart(0.6f)
                setVignetteEnd(0.9f)
            })
            add(GPUImageGrainFilter(0.3f))          // Gritty grain effect
        })
    }

    fun createHB2Filter(): GPUImageFilterGroup {
        // Gritty texture with deep shadows
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageContrastFilter(1.5f))       // Heavy contrast
            add(GPUImageHighlightShadowFilter(1.0f, 0.7f))  // Deep shadows
            add(GPUImageSharpenFilter(2.0f))        // Sharp details
            add(GPUImageGrainFilter(0.4f))          // Heavy grain
        })
    }

    // E-Series (Essentials) Filters
    fun createE1Filter(): GPUImageFilterGroup {
        // Natural, clean look
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageBrightnessFilter(0.05f))    // Slight brightness boost
            add(GPUImageContrastFilter(1.1f))       // Subtle contrast
            add(GPUImageSaturationFilter(1.1f))     // Slight saturation boost
        })
    }

    fun createE3Filter(): GPUImageFilterGroup {
        // Bright and airy
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageBrightnessFilter(0.15f))    // Brightness boost
            add(GPUImageContrastFilter(0.95f))      // Slightly reduced contrast
            add(GPUImageHighlightShadowFilter(1.2f, 0.9f))  // Lifted shadows
            add(GPUImageWhiteBalanceFilter(5500f, 0.0f))    // Clean white balance
        })
    }

    // K-Series (Kodak-inspired) Filters
    fun createK1Filter(): GPUImageFilterGroup {
        // Warm, golden tones
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageColorMatrixFilter(1.0f).apply {
                setColorMatrix(floatArrayOf(
                    1.2f, 0.0f, 0.0f, 0.0f,  // Strong red
                    0.0f, 1.1f, 0.0f, 0.0f,  // Boosted green
                    0.0f, 0.0f, 0.8f, 0.0f,  // Reduced blue
                    0.0f, 0.0f, 0.0f, 1.0f
                ))
            })
            add(GPUImageHighlightShadowFilter(1.1f, 0.8f))  // Rich shadows
            add(GPUImageGrainFilter(0.2f))                  // Film grain
        })
    }

    fun createK2Filter(): GPUImageFilterGroup {
        // Rich shadows with warm highlights
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageToneCurveFilter().apply {
                setRgbCompositeControlPoints(arrayOf(
                    floatArrayOf(0f, 0f),
                    floatArrayOf(0.25f, 0.2f),
                    floatArrayOf(0.5f, 0.5f),
                    floatArrayOf(0.75f, 0.8f),
                    floatArrayOf(1f, 1f)
                ))
            })
            add(GPUImageSepiaToneFilter(0.2f))      // Slight warmth
            add(GPUImageGrainFilter(0.25f))         // Film grain
        })
    }

    // G-Series (Black & White) Filters
    fun createG1Filter(): GPUImageFilterGroup {
        // Classic monochrome
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageGrayscaleFilter())
            add(GPUImageContrastFilter(1.2f))
            add(GPUImageHighlightShadowFilter(1.1f, 0.9f))
            add(GPUImageGrainFilter(0.2f))
        })
    }

    fun createG2Filter(): GPUImageFilterGroup {
        // High contrast B&W
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageGrayscaleFilter())
            add(GPUImageContrastFilter(1.4f))
            add(GPUImageHighlightShadowFilter(1.2f, 0.7f))
            add(GPUImageVignetteFilter().apply {
                setVignetteStart(0.7f)
                setVignetteEnd(0.9f)
            })
            add(GPUImageGrainFilter(0.3f))
        })
    }

    // Legacy Presets
    fun createC1Filter(): GPUImageFilterGroup {
        // Nature/Landscape enhancement
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageRGBFilter().apply {
                setGreen(1.2f)  // Boost greens
                setBlue(1.1f)   // Slight blue boost
            })
            add(GPUImageContrastFilter(1.1f))
            add(GPUImageSaturationFilter(1.2f))
        })
    }

    fun createM5Filter(): GPUImageFilterGroup {
        // Matte finish
        return GPUImageFilterGroup(ArrayList<GPUImageFilter>().apply {
            add(GPUImageSaturationFilter(0.8f))     // Desaturated
            add(GPUImageHighlightShadowFilter(0.9f, 0.9f))  // Matte look
            add(GPUImageContrastFilter(1.1f))
            add(GPUImageGrainFilter(0.15f))         // Subtle grain
        })
    }
}
