package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.vectrix.core.Vector3f;

/**
 * DLE-compatible ambient light payload from sky time-of-day state.
 */
public record AmbientLightData(Vector3f color, float intensity) {
}
