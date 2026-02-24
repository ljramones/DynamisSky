package org.dynamissky.core.noise;

/**
 * Placeholder noise interface for cloud/weather density fields.
 */
@FunctionalInterface
public interface NoiseProvider {
    float sample(float x, float y, float z);

    NoiseProvider ZERO = (x, y, z) -> 0f;
}
