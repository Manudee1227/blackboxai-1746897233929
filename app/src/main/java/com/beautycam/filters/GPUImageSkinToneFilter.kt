package com.beautycam.filters

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

class GPUImageSkinToneFilter : GPUImageFilter() {
    private var toneLevel: Float = 0f
    
    fun setToneLevel(tone: Float) {
        this.toneLevel = tone
        setFloat(getHandle("toneLevel"), tone)
    }

    override fun getVertexShader(): String {
        return NO_FILTER_VERTEX_SHADER
    }

    override fun getFragmentShader(): String {
        return """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform lowp float toneLevel;
            
            void main() {
                lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);
                
                // Detect skin tones (approximate)
                lowp float r = textureColor.r;
                lowp float g = textureColor.g;
                lowp float b = textureColor.b;
                lowp float skinMask = (r > 0.4 && g > 0.25 && b > 0.2 && 
                                     r > g && r > b && 
                                     abs(r - g) > 0.01) ? 1.0 : 0.0;
                
                // Adjust skin tone
                lowp vec3 skinAdjust = mix(textureColor.rgb, 
                                         vec3(1.0, 0.8, 0.7), 
                                         toneLevel * skinMask);
                
                gl_FragColor = vec4(skinAdjust, textureColor.a);
            }
        """
    }
}
