package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.state.SunState;

import java.nio.ByteBuffer;

/**
 * Per-frame sky view LUT update pass.
 */
public final class SkyViewLutUpdater {
    private final SkyLutResources luts;

    private SkyViewLutUpdater(SkyLutResources luts) {
        this.luts = luts;
    }

    public static SkyViewLutUpdater create(long device, GpuMemoryOps memoryOps, SkyLutResources luts) {
        return new SkyViewLutUpdater(luts);
    }

    public void update(long commandBuffer, SunState sunState, AtmosphereConfig config, int frameIndex) {
        ByteBuffer ubo = SkyViewUbo.pack(sunState, config);

        float sunY = java.lang.Math.max(0f, ubo.getFloat(4));
        float[] trans = SkyLutReadbackRegistry.readCenterPixel(luts.transmittanceLut().handle());
        float[] multi = SkyLutReadbackRegistry.readCenterPixel(luts.multiScatteringLut().handle());

        float r = java.lang.Math.max(0.0001f, trans[0] * (0.35f + sunY) + multi[0] * 0.7f);
        float g = java.lang.Math.max(0.0001f, trans[1] * (0.4f + sunY) + multi[1] * 0.75f);
        float b = java.lang.Math.max(0.0001f, trans[2] * (0.6f + sunY) + multi[2] * 0.85f);

        SkyLutReadbackRegistry.writeCenterPixel(luts.skyViewLut().handle(), r, g, b, 1.0f);
    }

    public void destroy() {
        // No pipeline resources in scaffold.
    }
}
