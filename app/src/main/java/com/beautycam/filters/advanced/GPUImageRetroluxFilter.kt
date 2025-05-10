package com.beautycam.filters.advanced

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import android.opengl.GLES20
import java.util.*

class GPUImageRetroluxFilter : GPUImageFilter() {
    private var intensity: Float = 0.5f
    private var grainIntensity: Float = 0.3f
    private var lightLeakIntensity: Float = 0.4f
    private var timeLocation: Int = 0
    private var random = Random()

    override fun onInit() {
        super.onInit()
        timeLocation = GLES20.glGetUniformLocation(program, "time")
    }

    fun setIntensity(value: Float) {
        intensity = value
        setFloat(getHandle("intensity"), intensity)
    }

    fun setGrainIntensity(value: Float) {
        grainIntensity = value
        setFloat(getHandle("grainIntensity"), grainIntensity)
    }

    fun setLightLeakIntensity(value: Float) {
        lightLeakIntensity = value
        setFloat(getHandle("lightLeakIntensity"), lightLeakIntensity)
    }

    override fun onDraw(textureId: Int, cubeBuffer: FloatArray, textureBuffer: FloatArray) {
        super.onDraw(textureId, cubeBuffer, textureBuffer)
        setFloat(timeLocation, random.nextFloat())
    }

    override fun getVertexShader(): String = NO_FILTER_VERTEX_SHADER

    override fun getFragmentShader(): String = """
        varying highp vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;
        uniform float intensity;
        uniform float grainIntensity;
        uniform float lightLeakIntensity;
        uniform float time;

        // Noise generation
        float rand(vec2 co) {
            return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
        }

        // Light leak effect
        vec4 getLightLeak(vec2 uv, float time) {
            vec2 center = vec2(0.5 + 0.5 * sin(time), 0.5 + 0.5 * cos(time));
            float dist = distance(uv, center);
            vec3 color = vec3(1.2, 0.8, 0.6); // Warm light color
            return vec4(color * (1.0 - dist), 1.0);
        }

        // Scratch effect
        float getScratch(vec2 uv, float time) {
            float scratch = rand(vec2(uv.y, time)) * step(0.98, uv.x);
            scratch *= sin(uv.y * 10.0 + time);
            return scratch;
        }

        void main() {
            vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
            
            // Add film grain
            vec2 noiseCoord = textureCoordinate * time;
            float noise = rand(noiseCoord) - 0.5;
            
            // Generate light leak
            vec4 lightLeak = getLightLeak(textureCoordinate, time);
            
            // Add scratches
            float scratch = getScratch(textureCoordinate, time);
            
            // Vintage color adjustment
            vec3 vintageColor = vec3(
                textureColor.r * 1.1,  // Boost reds slightly
                textureColor.g * 0.9,  // Reduce greens
                textureColor.b * 0.8   // Reduce blues more
            );
            
            // Combine effects
            vec3 result = vintageColor;
            result += noise * grainIntensity;
            result += lightLeak.rgb * lightLeakIntensity;
            result += vec3(scratch) * 0.3;
            
            // Final blend
            gl_FragColor = vec4(mix(textureColor.rgb, result, intensity), textureColor.a);
        }
    """
}
