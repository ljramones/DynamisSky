package org.dynamissky.vulkan;

import org.dynamissky.vulkan.lut.CameraState;

/**
 * Per-frame context for sky service updates.
 */
public record SkyFrameContext(
        long commandBuffer,
        CameraState camera,
        int frameIndex,
        float deltaSeconds) {

    public static SkyFrameContext of(long commandBuffer, CameraState camera, int frameIndex, float deltaSeconds) {
        return new SkyFrameContext(commandBuffer, camera, frameIndex, deltaSeconds);
    }
}
