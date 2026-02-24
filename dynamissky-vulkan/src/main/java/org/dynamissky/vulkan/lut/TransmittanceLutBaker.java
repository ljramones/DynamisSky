package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;

import java.nio.ByteBuffer;

/**
 * Transmittance LUT bake pass (compute dispatch equivalent).
 */
public final class TransmittanceLutBaker {
    private final long device;
    private final GpuMemoryOps memoryOps;
    private final SkyLutResources luts;

    private TransmittanceLutBaker(long device, GpuMemoryOps memoryOps, SkyLutResources luts) {
        this.device = device;
        this.memoryOps = memoryOps;
        this.luts = luts;
    }

    public static TransmittanceLutBaker create(long device, GpuMemoryOps memoryOps, SkyLutResources luts) {
        return new TransmittanceLutBaker(device, memoryOps, luts);
    }

    public void bake(long commandBuffer, AtmosphereConfig config, int frameIndex) {
        ByteBuffer ubo = AtmosphereUbo.pack(config);
        float densityScale = ubo.getFloat(12);

        float transmittance = clamp01((float) java.lang.Math.exp(-0.12f * densityScale));
        float r = transmittance;
        float g = transmittance * 0.95f;
        float b = transmittance * 0.9f;

        SkyLutReadbackRegistry.writeCenterPixel(luts.transmittanceLut().handle(), r, g, b, 1.0f);
    }

    public void destroy() {
        // No pipeline resources in scaffold.
    }

    private static float clamp01(float v) {
        return java.lang.Math.max(0f, java.lang.Math.min(1f, v));
    }
}
