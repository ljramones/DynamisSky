package org.dynamissky.api.state;

import org.dynamissky.api.ColorRgb;
import org.dynamissky.api.Vec3;

/**
 * Moon position and appearance state.
 */
public record MoonState(
        Vec3 direction,
        ColorRgb color,
        float intensity,
        float phase,
        float angularRadiusDegrees) {

    public MoonState {
        if (direction == null || color == null) {
            throw new IllegalArgumentException("direction and color are required");
        }
        if (!Float.isFinite(intensity) || intensity < 0f) {
            throw new IllegalArgumentException("intensity must be finite and >= 0");
        }
        if (!Float.isFinite(phase) || phase < 0f || phase > 1f) {
            throw new IllegalArgumentException("phase must be in [0,1]");
        }
        if (!Float.isFinite(angularRadiusDegrees) || angularRadiusDegrees <= 0f) {
            throw new IllegalArgumentException("angularRadiusDegrees must be > 0");
        }
    }
}
