package org.dynamissky.core.color;

import org.vectrix.color.ColorMathf;
import org.vectrix.core.Vector3f;

/**
 * Blackbody color temperature approximation (Tanner Helland) with linear output.
 */
public final class KelvinToRgb {
    private KelvinToRgb() {
    }

    public static Vector3f toLinearRgb(float kelvin, Vector3f dest) {
        if (dest == null) {
            throw new IllegalArgumentException("dest is required");
        }
        if (!Float.isFinite(kelvin) || kelvin <= 0f) {
            throw new IllegalArgumentException("kelvin must be finite and > 0");
        }

        // TODO: upstream to vectrix ColorSciencef
        float temperature = clamp(kelvin, 1000f, 40000f) / 100f;

        float red;
        float green;
        float blue;

        if (temperature <= 66f) {
            red = 255f;
            green = 99.4708f * (float) java.lang.Math.log(temperature) - 161.11957f;
            blue = temperature <= 19f
                    ? 0f
                    : 138.51773f * (float) java.lang.Math.log(temperature - 10f) - 305.0448f;
        } else {
            red = 329.69873f * (float) java.lang.Math.pow(temperature - 60f, -0.13320476f);
            green = 288.12216f * (float) java.lang.Math.pow(temperature - 60f, -0.075514846f);
            blue = 255f;
        }

        float sr = clamp(red / 255f, 0f, 1f);
        float sg = clamp(green / 255f, 0f, 1f);
        float sb = clamp(blue / 255f, 0f, 1f);

        dest.set(sr, sg, sb);
        return ColorMathf.srgbToLinear(dest, dest);
    }

    private static float clamp(float value, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }
}
