package com.beautycam.filters

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import android.opengl.GLES20
import java.util.*

class GPUImageGrainFilter(private var intensity: Float = 0.5f) : GPUImageFilter() {
    private var intensityLocation: Int = 0
    private var timeLocation: Int = 0
    private var random = Random()

    override fun onInit() {
        super.onInit()
        intensityLocation = GLES20.glGetUniformLocation(program, "intensity")
        timeLocation = GLES20.glGetUniformLocation(program, "time")
    }

    override fun onInitialized() {
        super.onInitialized()
        setIntensity(intensity)
    }

    fun setIntensity(intensity: Float) {
        this.intensity = intensity
        setFloat(intensityLocation, intensity)
    }

    override fun onDraw(textureId: Int, cubeBuffer: FloatArray, textureBuffer: FloatArray) {
        super.onDraw(textureId, cubeBuffer, textureBuffer)
        setFloat(timeLocation, random.nextFloat())
    }

    override fun getVertexShader(): String {
        return NO_FILTER_VERTEX_SHADER
    }

    override fun getFragmentShader(): String {
        return """
            precision highp float;
            varying vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform float intensity;
            uniform float time;
            
            float rand(vec2 co) {
                return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
            }
            
            void main() {
                vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                
                // Generate noise
                vec2 noiseCoord = textureCoordinate * time;
                float noise = rand(noiseCoord) - 0.5;
                
                // Apply noise with intensity
                vec3 result = textureColor.rgb + (noise * intensity);
                
                gl_FragColor = vec4(result, textureColor.a);
            }
        """
    }
}
