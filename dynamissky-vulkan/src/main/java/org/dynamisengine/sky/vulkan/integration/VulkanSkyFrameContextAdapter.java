package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.sky.vulkan.SkyFrameContext;
import org.dynamisengine.sky.vulkan.lut.CameraState;

/**
 * Adapts DLE frame data to SkyFrameContext.
 */
public final class VulkanSkyFrameContextAdapter {
    private VulkanSkyFrameContextAdapter() {
    }

    public static SkyFrameContext adapt(long commandBuffer,
                                        VulkanCameraState dleCamera,
                                        float deltaSeconds,
                                        int frameIndex) {
        CameraState camera = new CameraState(
                dleCamera.position(),
                dleCamera.nearPlane(),
                dleCamera.farPlane(),
                dleCamera.frustumTL(),
                dleCamera.frustumTR(),
                dleCamera.frustumBL());
        return SkyFrameContext.of(commandBuffer, camera, frameIndex, deltaSeconds);
    }
}
