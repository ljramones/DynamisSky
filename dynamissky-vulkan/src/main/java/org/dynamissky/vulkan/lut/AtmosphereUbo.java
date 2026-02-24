package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * std140-packed atmosphere uniform payload.
 */
public final class AtmosphereUbo {
    public static final int SIZE_BYTES = 64;

    private AtmosphereUbo() {
    }

    public static ByteBuffer pack(AtmosphereConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is required");
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        // vec3 rayleighScatter + pad
        putVec3(buffer, 5.8e-3f, 13.5e-3f, 33.1e-3f);
        buffer.putFloat(config.rayleighScaleHeightKm());

        // mieScatter, mieAbsorb, mieHeight, mieAnisotropy
        buffer.putFloat(3.996e-3f);
        buffer.putFloat(4.4e-3f);
        buffer.putFloat(config.mieScaleHeightKm());
        buffer.putFloat(config.mieAnisotropyG());

        // vec3 ozoneAbsorb + pad
        putVec3(buffer, 0.00065f, 0.001881f, 0.000085f);
        buffer.putFloat(0f);

        // planetRadius, atmosphereHeight, + pad to 64
        buffer.putFloat(config.planetRadiusKm());
        buffer.putFloat(config.atmosphereRadiusKm() - config.planetRadiusKm());
        buffer.putFloat(config.ozoneLayerCenterKm());
        buffer.putFloat(config.ozoneLayerWidthKm());

        buffer.flip();
        return buffer;
    }

    private static void putVec3(ByteBuffer buffer, float x, float y, float z) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
    }
}
