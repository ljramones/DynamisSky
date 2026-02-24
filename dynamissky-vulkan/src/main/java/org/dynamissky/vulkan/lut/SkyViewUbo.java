package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.state.SunState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * std140-packed per-frame UBO for sky view LUT pass.
 */
public final class SkyViewUbo {
    public static final int SIZE_BYTES = 48;

    private SkyViewUbo() {
    }

    public static ByteBuffer pack(SunState sunState, AtmosphereConfig config) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        buffer.putFloat(sunState.direction().x());
        buffer.putFloat(sunState.direction().y());
        buffer.putFloat(sunState.direction().z());
        buffer.putFloat(sunState.intensity());

        buffer.putFloat(5.8e-3f);
        buffer.putFloat(13.5e-3f);
        buffer.putFloat(33.1e-3f);
        buffer.putFloat(config.mieAnisotropyG());

        buffer.putFloat(config.planetRadiusKm());
        buffer.putFloat(config.atmosphereRadiusKm() - config.planetRadiusKm());
        buffer.putFloat(0f);
        buffer.putFloat(0f);

        buffer.flip();
        return buffer;
    }
}
