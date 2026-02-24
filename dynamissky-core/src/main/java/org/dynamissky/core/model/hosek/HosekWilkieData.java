package org.dynamissky.core.model.hosek;

/**
 * Embedded coefficient tables for Hosek-Wilkie approximation.
 */
public final class HosekWilkieData {
    private HosekWilkieData() {
    }

    public static final float[] RGB_COEFFICIENTS = {
            0.1787f, -1.4630f, -0.3554f, 0.4275f,
            -0.0227f, 5.3251f, 0.1206f, -2.5771f,
            -0.0670f, 0.3703f, 0.7017f, -1.1687f
    };

    public static final float[] LUMINANCE_COEFFICIENTS = {
            0.4669f, 3.5556f, -2.7152f, -1.3081f,
            -0.0669f, 0.0008f, 0.2122f, -0.8989f
    };
}
