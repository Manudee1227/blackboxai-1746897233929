package com.beautycam.filters.video

import android.content.Context

class VideoFilterManager(private val context: Context) {
    private var currentFilter: VideoFilter? = null
    private var currentFilterType = FilterType.NONE
    private var intensity: Float = 1.0f

    enum class FilterType {
        NONE,
        BEAUTY,
        VINTAGE,
        DRAMATIC
    }

    fun setFilter(type: FilterType) {
        // Release current filter if exists
        currentFilter?.release()

        // Create new filter
        currentFilter = when (type) {
            FilterType.BEAUTY -> BeautyVideoFilter(context)
            FilterType.VINTAGE -> VintageVideoFilter(context)
            FilterType.DRAMATIC -> DramaticVideoFilter(context)
            FilterType.NONE -> null
        }

        currentFilterType = type
        
        // Apply current intensity to new filter
        currentFilter?.setIntensity(intensity)
    }

    fun setIntensity(value: Float) {
        intensity = value.coerceIn(0f, 1f)
        currentFilter?.setIntensity(intensity)
    }

    fun getCurrentFilter(): VideoFilter? = currentFilter

    fun getCurrentFilterType(): FilterType = currentFilterType

    fun release() {
        currentFilter?.release()
        currentFilter = null
        currentFilterType = FilterType.NONE
    }

    fun onDraw(textureId: Int, cubeBuffer: FloatArray, textureBuffer: FloatArray) {
        currentFilter?.onDraw(textureId, cubeBuffer, textureBuffer)
    }

    companion object {
        private const val TAG = "VideoFilterManager"
    }
}
