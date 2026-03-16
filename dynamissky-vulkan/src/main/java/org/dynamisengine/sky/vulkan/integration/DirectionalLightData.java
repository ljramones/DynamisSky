package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.vectrix.core.Matrix4f;
import org.dynamisengine.vectrix.core.Vector3f;

/**
 * DLE-compatible directional light payload from sky state.
 */
public record DirectionalLightData(
        Vector3f direction,
        Vector3f color,
        float intensity,
        Matrix4f shadowMatrix) {
}
