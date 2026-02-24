package org.dynamissky.api;

/**
 * Linear RGB color value.
 */
public record ColorRgb(float r, float g, float b) {
    public static final ColorRgb WHITE = new ColorRgb(1f, 1f, 1f);

    public ColorRgb {
        if (!Float.isFinite(r) || !Float.isFinite(g) || !Float.isFinite(b)) {
            throw new IllegalArgumentException("Color channels must be finite");
        }
        if (r < 0f || g < 0f || b < 0f) {
            throw new IllegalArgumentException("Color channels must be >= 0");
        }
    }
}
