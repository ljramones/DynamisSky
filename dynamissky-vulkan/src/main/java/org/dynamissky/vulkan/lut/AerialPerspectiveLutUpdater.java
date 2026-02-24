package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.state.SunState;

import java.nio.ByteBuffer;

/**
 * Per-frame aerial perspective volume LUT update pass.
 */
public final class AerialPerspectiveLutUpdater {
    private final SkyLutResources luts;

    private AerialPerspectiveLutUpdater(SkyLutResources luts) {
        this.luts = luts;
    }

    public static AerialPerspectiveLutUpdater create(long device, GpuMemoryOps memoryOps, SkyLutResources luts) {
        return new AerialPerspectiveLutUpdater(luts);
    }

    public void update(long commandBuffer, SunState sunState, AtmosphereConfig config, CameraState camera, int frameIndex) {
        ByteBuffer ubo = AerialUbo.pack(sunState, camera);
        float nearPlane = ubo.getFloat(28);
        float farPlane = ubo.getFloat(80);

        float[] trans = SkyLutReadbackRegistry.readCenterPixel(luts.transmittanceLut().handle());
        float depthWeight = java.lang.Math.min(1f, nearPlane / java.lang.Math.max(nearPlane, farPlane));

        float r = java.lang.Math.max(0.0001f, (1f - trans[0]) * 0.5f + depthWeight * 0.1f);
        float g = java.lang.Math.max(0.0001f, (1f - trans[1]) * 0.6f + depthWeight * 0.1f);
        float b = java.lang.Math.max(0.0001f, (1f - trans[2]) * 0.8f + depthWeight * 0.1f);
        float a = java.lang.Math.max(0.0001f, java.lang.Math.min(1f, 0.2f + trans[1] * 0.7f));

        SkyLutReadbackRegistry.writeCenterPixel(luts.aerialPerspectiveLut().handle(), r, g, b, a);
    }

    public void destroy() {
        // No pipeline resources in scaffold.
    }
}
