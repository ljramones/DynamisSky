package org.dynamissky.api;

/**
 * Lightweight, dependency-free 3D vector for API value types.
 */
public record Vec3(float x, float y, float z) {
    public static final Vec3 ZERO = new Vec3(0f, 0f, 0f);

    public Vec3 {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            throw new IllegalArgumentException("Vec3 components must be finite");
        }
    }
}
