package com.beautycam.filters.video

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageColorMatrixFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrainFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup

class VintageVideoFilter(context: Context) : VideoFilter(context) {
    override fun setupFilter(context: Context) {
        val filterGroup = GPUImageFilterGroup()
        
        // Add sepia effect
        val sepiaMatrix = GPUImageColorMatrixFilter().apply {
            setColorMatrix(floatArrayOf(
                1.3f, -0.3f, 1.1f, 0.0f,
                0.0f, 1.2f, 0.2f, 0.0f,
                -0.1f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
            ))
        }
        filterGroup.addFilter(sepiaMatrix)
        
        // Add film grain
        val grainFilter = GPUImageGrainFilter().apply {
            setIntensity(0.5f)
        }
        filterGroup.addFilter(grainFilter)
        
        filter = filterGroup
    }

    override fun setIntensity(intensity: Float) {
        (filter as? GPUImageFilterGroup)?.filters?.forEach { filter ->
            when (filter) {
                is GPUImageGrainFilter -> filter.setIntensity(intensity * 0.5f)
                is GPUImageColorMatrixFilter -> {
                    val sepiaStrength = intensity * 0.3f
                    filter.setColorMatrix(floatArrayOf(
                        1.3f + sepiaStrength, -0.3f, 1.1f, 0.0f,
                        0.0f, 1.2f + sepiaStrength, 0.2f, 0.0f,
                        -0.1f, 0.0f, 1.0f + sepiaStrength, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f
                    ))
                }
            }
        }
    }
}
