package org.dynamissky.vulkan.integration;

import org.vectrix.core.Matrix4f;
import org.vectrix.core.Vector3f;

/**
 * DLE-compatible directional light payload from sky state.
 */
public record DirectionalLightData(
        Vector3f direction,
        Vector3f color,
        float intensity,
        Matrix4f shadowMatrix) {
}
