package com.beautycam.filters.video

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBeautyFilter

class BeautyVideoFilter(context: Context) : VideoFilter(context) {
    override fun setupFilter(context: Context) {
        filter = GPUImageBeautyFilter().apply {
            setBeautyLevel(1.0f)
        }
    }

    override fun setIntensity(intensity: Float) {
        (filter as? GPUImageBeautyFilter)?.setBeautyLevel(intensity)
    }
}
