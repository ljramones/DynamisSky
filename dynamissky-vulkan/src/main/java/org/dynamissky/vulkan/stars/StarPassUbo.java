package org.dynamissky.vulkan.stars;

import org.vectrix.core.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Per-frame UBO payload for star field rendering.
 */
public record StarPassUbo(ByteBuffer bytes) {
    public static final int SIZE_BYTES = 80;

    public static StarPassUbo of(Matrix4f viewProj,
                                 float starVisibility,
                                 float twinkleTime,
                                 float minMagnitudeBrightness) {
        return new StarPassUbo(pack(viewProj, starVisibility, twinkleTime, minMagnitudeBrightness));
    }

    public static ByteBuffer pack(Matrix4f viewProj,
                                  float starVisibility,
                                  float twinkleTime,
                                  float minMagnitudeBrightness) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        buffer.putFloat(viewProj.m00()).putFloat(viewProj.m01()).putFloat(viewProj.m02()).putFloat(viewProj.m03());
        buffer.putFloat(viewProj.m10()).putFloat(viewProj.m11()).putFloat(viewProj.m12()).putFloat(viewProj.m13());
        buffer.putFloat(viewProj.m20()).putFloat(viewProj.m21()).putFloat(viewProj.m22()).putFloat(viewProj.m23());
        buffer.putFloat(viewProj.m30()).putFloat(viewProj.m31()).putFloat(viewProj.m32()).putFloat(viewProj.m33());

        buffer.putFloat(starVisibility);
        buffer.putFloat(twinkleTime);
        buffer.putFloat(minMagnitudeBrightness);
        buffer.putFloat(0f);

        buffer.flip();
        return buffer;
    }
}
