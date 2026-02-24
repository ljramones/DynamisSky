package org.dynamissky.vulkan.integration;

import org.vectrix.core.Vector3f;

/**
 * DLE-side camera state consumed by sky frame adapter.
 */
public record VulkanCameraState(
        Vector3f position,
        float nearPlane,
        float farPlane,
        Vector3f frustumTL,
        Vector3f frustumTR,
        Vector3f frustumBL) {
}
