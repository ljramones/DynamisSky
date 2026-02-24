package org.dynamissky.core.noise;

/**
 * Placeholder noise configuration for upcoming volumetric cloud work.
 */
public record NoiseConfig(float frequency, int octaves) {
    public NoiseConfig {
        if (!Float.isFinite(frequency) || frequency <= 0f) {
            throw new IllegalArgumentException("frequency must be finite and > 0");
        }
        if (octaves <= 0) {
            throw new IllegalArgumentException("octaves must be > 0");
        }
    }
}
