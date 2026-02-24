package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;

/**
 * Multi-scattering LUT bake pass (Hillaire-inspired approximation scaffold).
 */
public final class MultiScatteringLutBaker {
    private final SkyLutResources luts;

    private MultiScatteringLutBaker(SkyLutResources luts) {
        this.luts = luts;
    }

    public static MultiScatteringLutBaker create(long device, GpuMemoryOps memoryOps, SkyLutResources luts) {
        return new MultiScatteringLutBaker(luts);
    }

    public void bake(long commandBuffer, AtmosphereConfig config, int frameIndex) {
        float[] transmittance = SkyLutReadbackRegistry.readCenterPixel(luts.transmittanceLut().handle());
        float attenuation = clamp01(1.0f - transmittance[1]);
        float r = java.lang.Math.max(0.0001f, attenuation * 0.55f);
        float g = java.lang.Math.max(0.0001f, attenuation * 0.65f);
        float b = java.lang.Math.max(0.0001f, attenuation * 0.85f);

        SkyLutReadbackRegistry.writeCenterPixel(luts.multiScatteringLut().handle(), r, g, b, 1.0f);
    }

    public void destroy() {
        // No pipeline resources in scaffold.
    }

    private static float clamp01(float v) {
        return java.lang.Math.max(0f, java.lang.Math.min(1f, v));
    }
}
