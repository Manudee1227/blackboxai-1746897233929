package com.beautycam.filters.advanced

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

class GPUImageHDRFilter : GPUImageFilter() {
    private var intensity: Float = 1.0f

    fun setIntensity(value: Float) {
        intensity = value
        setFloat(getHandle("intensity"), intensity)
    }

    override fun getVertexShader(): String = NO_FILTER_VERTEX_SHADER

    override fun getFragmentShader(): String = """
        varying highp vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;
        uniform float intensity;

        float getLuma(vec3 color) {
            return dot(color, vec3(0.299, 0.587, 0.114));
        }

        void main() {
            vec4 source = texture2D(inputImageTexture, textureCoordinate);
            float luma = getLuma(source.rgb);
            
            // Enhance local contrast
            float shadow = clamp((pow(luma, 1.0/(2.2 + intensity)) + (-0.76)*pow(luma, 2.0/(2.2 + intensity))) * 2.0, 0.0, 1.0);
            float highlight = clamp((1.0 - (pow(1.0-luma, 1.0/(2.2 + intensity)) + (-0.8)*pow(1.0-luma, 2.0/(2.2 + intensity)))) * 2.0, 0.0, 1.0);
            vec3 result = vec3(0.0, 0.0, 0.0);
            
            // Blend shadows and highlights
            result.rgb = mix(source.rgb * shadow, source.rgb * highlight, luma);
            
            gl_FragColor = vec4(mix(source.rgb, result.rgb, intensity), source.a);
        }
    """
}
