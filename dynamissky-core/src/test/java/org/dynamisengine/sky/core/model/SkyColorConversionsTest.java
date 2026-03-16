package org.dynamisengine.sky.core.model;

import org.junit.jupiter.api.Test;
import org.vectrix.color.ColorSciencef;
import org.vectrix.core.Vector3f;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkyColorConversionsTest {

    private static final float TOL = 0.02f;

    /** Convert linear sRGB to xyY via XYZ. */
    private static Vector3f linearSrgbToXyY(float r, float g, float b) {
        Vector3f xyz = ColorSciencef.linearSrgbToXyz(r, g, b, new Vector3f());
        float X = xyz.x, Y = xyz.y, Z = xyz.z;
        float sum = X + Y + Z;
        if (sum < 1e-6f) {
            return new Vector3f(0f, 0f, 0f);
        }
        return new Vector3f(X / sum, Y / sum, Y);
    }

    // ===== Round-trip: RGB -> xyY -> RGB =====

    @Test
    void roundTripWhite() {
        Vector3f xyY = linearSrgbToXyY(1f, 1f, 1f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(1f, rgb.x, TOL);
        assertEquals(1f, rgb.y, TOL);
        assertEquals(1f, rgb.z, TOL);
    }

    @Test
    void roundTripMidGray() {
        Vector3f xyY = linearSrgbToXyY(0.5f, 0.5f, 0.5f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0.5f, rgb.x, TOL);
        assertEquals(0.5f, rgb.y, TOL);
        assertEquals(0.5f, rgb.z, TOL);
    }

    @Test
    void roundTripRed() {
        Vector3f xyY = linearSrgbToXyY(1f, 0f, 0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(1f, rgb.x, TOL);
        assertEquals(0f, rgb.y, TOL);
        assertEquals(0f, rgb.z, TOL);
    }

    @Test
    void roundTripGreen() {
        Vector3f xyY = linearSrgbToXyY(0f, 1f, 0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0f, rgb.x, TOL);
        assertEquals(1f, rgb.y, TOL);
        assertEquals(0f, rgb.z, TOL);
    }

    @Test
    void roundTripBlue() {
        Vector3f xyY = linearSrgbToXyY(0f, 0f, 1f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0f, rgb.x, TOL);
        assertEquals(0f, rgb.y, TOL);
        assertEquals(1f, rgb.z, TOL);
    }

    // ===== Known color point: D65 white =====

    @Test
    void d65WhitePointProducesNearWhiteRgb() {
        Vector3f xyY = new Vector3f(0.3127f, 0.3290f, 1.0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(1f, rgb.x, 0.05f);
        assertEquals(1f, rgb.y, 0.05f);
        assertEquals(1f, rgb.z, 0.05f);
    }

    // ===== Black / zero luminance =====

    @Test
    void zeroLuminanceProducesBlack() {
        Vector3f xyY = new Vector3f(0.3127f, 0.3290f, 0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0f, rgb.x, 1e-6f);
        assertEquals(0f, rgb.y, 1e-6f);
        assertEquals(0f, rgb.z, 1e-6f);
    }

    @Test
    void zeroYChromaticityProducesBlack() {
        Vector3f xyY = new Vector3f(0.3f, 0f, 1.0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0f, rgb.x, 1e-6f);
        assertEquals(0f, rgb.y, 1e-6f);
        assertEquals(0f, rgb.z, 1e-6f);
    }

    // ===== Very low luminance =====

    @Test
    void veryLowLuminanceProducesNearBlack() {
        Vector3f xyY = new Vector3f(0.3127f, 0.3290f, 0.001f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertEquals(0f, rgb.x, 0.01f);
        assertEquals(0f, rgb.y, 0.01f);
        assertEquals(0f, rgb.z, 0.01f);
    }

    // ===== Saturated colors stay finite =====

    @Test
    void highlySaturatedChromaticityProducesFiniteResult() {
        Vector3f xyY = new Vector3f(0.15f, 0.06f, 0.5f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertTrue(Float.isFinite(rgb.x));
        assertTrue(Float.isFinite(rgb.y));
        assertTrue(Float.isFinite(rgb.z));
    }

    // ===== Null arguments =====

    @Test
    void rejectsNullXyY() {
        assertThrows(IllegalArgumentException.class,
                () -> SkyColorConversions.xyYToLinearSrgb(null, new Vector3f()));
    }

    @Test
    void rejectsNullDest() {
        assertThrows(IllegalArgumentException.class,
                () -> SkyColorConversions.xyYToLinearSrgb(new Vector3f(), null));
    }

    // ===== linearToSrgb / srgbToLinear round-trip =====

    @Test
    void linearSrgbRoundTrip() {
        Vector3f linear = new Vector3f(0.5f, 0.2f, 0.8f);
        Vector3f srgb = SkyColorConversions.linearToSrgb(linear, new Vector3f());
        Vector3f back = SkyColorConversions.srgbToLinear(srgb, new Vector3f());
        assertEquals(0.5f, back.x, TOL);
        assertEquals(0.2f, back.y, TOL);
        assertEquals(0.8f, back.z, TOL);
    }

    // ===== High luminance =====

    @Test
    void highLuminanceProducesLargeRgb() {
        Vector3f xyY = new Vector3f(0.3127f, 0.3290f, 10.0f);
        Vector3f rgb = SkyColorConversions.xyYToLinearSrgb(xyY, new Vector3f());
        assertTrue(Float.isFinite(rgb.x));
        assertTrue(rgb.x > 1f, "R > 1 for high luminance");
        assertTrue(Float.isFinite(rgb.y));
        assertTrue(rgb.y > 1f);
        assertTrue(Float.isFinite(rgb.z));
        assertTrue(rgb.z > 1f);
    }
}
