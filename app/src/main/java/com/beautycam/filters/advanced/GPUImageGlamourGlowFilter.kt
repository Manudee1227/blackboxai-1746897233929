package com.beautycam.filters.advanced

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class GPUImageGlamourGlowFilter : GPUImageFilter() {
    private var intensity: Float = 0.5f
    private var radius: Float = 3.0f

    fun setIntensity(value: Float) {
        intensity = value
        setFloat(getHandle("intensity"), intensity)
    }

    fun setRadius(value: Float) {
        radius = value
        setFloat(getHandle("radius"), radius)
    }

    override fun getVertexShader(): String = NO_FILTER_VERTEX_SHADER

    override fun getFragmentShader(): String = """
        varying highp vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;
        uniform float intensity;
        uniform float radius;

        void main() {
            vec4 centralColor = texture2D(inputImageTexture, textureCoordinate);
            
            // Create soft glow effect
            float offset = radius / 100.0;
            vec4 sum = vec4(0.0);
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(-offset, -offset)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(0.0, -offset)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(offset, -offset)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(-offset, 0.0)) * 0.125;
            sum += centralColor * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(offset, 0.0)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(-offset, offset)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(0.0, offset)) * 0.125;
            sum += texture2D(inputImageTexture, textureCoordinate + vec2(offset, offset)) * 0.125;
            
            // Blend original with glow
            vec4 glowColor = sum * 2.0;
            glowColor = pow(glowColor, vec4(0.75)); // Soften the glow
            
            // Add slight warmth to the glow
            vec4 warmGlow = glowColor * vec4(1.1, 1.05, 1.0, 1.0);
            
            gl_FragColor = mix(centralColor, warmGlow, intensity);
        }
    """
}
