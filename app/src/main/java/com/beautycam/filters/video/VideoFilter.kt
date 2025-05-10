package com.beautycam.filters.video

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

abstract class VideoFilter(context: Context) {
    protected var filter: GPUImageFilter? = null
    protected var surfaceTexture: SurfaceTexture? = null
    protected var textureId: Int = -1

    init {
        setupFilter(context)
    }

    protected abstract fun setupFilter(context: Context)

    open fun onDraw(textureId: Int, cubeBuffer: FloatArray, textureBuffer: FloatArray) {
        filter?.let { gpuFilter ->
            if (!gpuFilter.isInitialized) {
                gpuFilter.init()
            }
            
            GLES20.glUseProgram(gpuFilter.program)
            
            // Set the input texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(gpuFilter.getHandle("inputImageTexture"), 0)
            
            // Draw the filter
            gpuFilter.onDraw(textureId, cubeBuffer, textureBuffer)
        }
    }

    open fun release() {
        filter?.destroy()
        filter = null
        surfaceTexture?.release()
        surfaceTexture = null
    }

    open fun setIntensity(intensity: Float) {
        // Override in subclasses if needed
    }
}
