package com.beautycam.filters.video

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.*

class DramaticVideoFilter(context: Context) : VideoFilter(context) {
    override fun setupFilter(context: Context) {
        val filterGroup = GPUImageFilterGroup()
        
        // Add contrast
        val contrastFilter = GPUImageContrastFilter(1.5f)
        filterGroup.addFilter(contrastFilter)
        
        // Add vignette
        val vignetteFilter = GPUImageVignetteFilter().apply {
            setVignetteStart(0.7f)
            setVignetteEnd(0.9f)
        }
        filterGroup.addFilter(vignetteFilter)
        
        // Add highlight/shadow adjustment
        val highlightShadowFilter = GPUImageHighlightShadowFilter().apply {
            setHighlights(0.7f)
            setShadows(0.3f)
        }
        filterGroup.addFilter(highlightShadowFilter)
        
        // Add saturation adjustment
        val saturationFilter = GPUImageSaturationFilter(1.2f)
        filterGroup.addFilter(saturationFilter)
        
        filter = filterGroup
    }

    override fun setIntensity(intensity: Float) {
        (filter as? GPUImageFilterGroup)?.filters?.forEach { filter ->
            when (filter) {
                is GPUImageContrastFilter -> filter.setContrast(1.0f + intensity * 0.5f)
                is GPUImageVignetteFilter -> {
                    filter.setVignetteStart(0.7f + intensity * 0.2f)
                    filter.setVignetteEnd(0.9f + intensity * 0.1f)
                }
                is GPUImageHighlightShadowFilter -> {
                    filter.setHighlights(0.7f + intensity * 0.3f)
                    filter.setShadows(0.3f - intensity * 0.2f)
                }
                is GPUImageSaturationFilter -> {
                    filter.setSaturation(1.0f + intensity * 0.2f)
                }
            }
        }
    }
}
