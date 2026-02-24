package org.dynamissky.core.model;

import org.vectrix.color.ColorMathf;
import org.vectrix.color.ColorSciencef;
import org.vectrix.core.Vector3f;

/**
 * Color conversion helpers for analytical sky models.
 */
public final class SkyColorConversions {
    private SkyColorConversions() {
    }

    public static Vector3f xyYToLinearSrgb(Vector3f xyY, Vector3f dest) {
        if (xyY == null || dest == null) {
            throw new IllegalArgumentException("xyY and dest are required");
        }

        float x = xyY.x;
        float y = xyY.y;
        float Y = xyY.z;
        if (y <= 1e-6f) {
            return dest.zero();
        }

        float X = (x * Y) / y;
        float Z = ((1f - x - y) * Y) / y;
        return ColorSciencef.xyzToLinearSrgb(X, Y, Z, dest);
    }

    public static Vector3f linearToSrgb(Vector3f linearRgb, Vector3f dest) {
        return ColorMathf.linearToSrgb(linearRgb, dest);
    }

    public static Vector3f srgbToLinear(Vector3f srgb, Vector3f dest) {
        return ColorMathf.srgbToLinear(srgb, dest);
    }
}
