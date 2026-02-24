package org.dynamissky.api.state;

import org.dynamissky.api.ColorRgb;
import org.dynamissky.api.Vec3;

/**
 * Directional light source state produced by sky simulation.
 */
public record SunState(
        Vec3 direction,
        ColorRgb color,
        float intensity,
        double azimuthDegrees,
        double altitudeDegrees) {

    public SunState {
        if (direction == null || color == null) {
            throw new IllegalArgumentException("direction and color are required");
        }
        if (!Float.isFinite(intensity) || intensity < 0f) {
            throw new IllegalArgumentException("intensity must be finite and >= 0");
        }
    }
}
