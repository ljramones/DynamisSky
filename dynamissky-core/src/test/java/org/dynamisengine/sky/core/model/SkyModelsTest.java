package org.dynamisengine.sky.core.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.dynamisengine.vectrix.core.Vector3f;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkyModelsTest {

    private static final Vector3f SUN_HIGH = new Vector3f(0f, 1f, 0f);
    private static final Vector3f SUN_45 = new Vector3f(0f, 0.707f, 0.707f);
    private static final Vector3f SUN_HORIZON = new Vector3f(0f, 0.01f, 1f);
    private static final Vector3f VIEW_ZENITH = new Vector3f(0f, 1f, 0f);
    private static final Vector3f VIEW_HORIZON = new Vector3f(0f, 0.01f, 1f);

    // ===== Non-negative output =====

    @ParameterizedTest
    @ValueSource(floats = {1f, 2f, 5f, 10f})
    void hosekOutputIsNonNegative(float turbidity) {
        var model = new HosekWilkieSkyModel(turbidity);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_45);
        assertTrue(xyY.x >= 0f, "x chromaticity non-negative");
        assertTrue(xyY.y >= 0f, "y chromaticity non-negative");
        assertTrue(xyY.z >= 0f, "luminance Y non-negative");
    }

    @ParameterizedTest
    @ValueSource(floats = {1f, 2f, 5f, 10f})
    void preethamOutputIsNonNegative(float turbidity) {
        var model = new PreethamSkyModel(turbidity);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_45);
        assertTrue(xyY.x >= 0f);
        assertTrue(xyY.y >= 0f);
        assertTrue(xyY.z >= 0f);
    }

    // ===== Sun direction produces peak luminance =====

    @Test
    void hosekPeakLuminanceNearSunDirection() {
        var model = new HosekWilkieSkyModel(2f);
        Vector3f atSun = model.evaluate(SUN_45, SUN_45);
        Vector3f awaySun = model.evaluate(new Vector3f(0f, 0.707f, -0.707f), SUN_45);
        assertTrue(atSun.z >= awaySun.z, "luminance at sun >= away from sun");
    }

    @Test
    void preethamPeakLuminanceNearSunDirection() {
        var model = new PreethamSkyModel(2f);
        Vector3f atSun = model.evaluate(SUN_45, SUN_45);
        Vector3f awaySun = model.evaluate(new Vector3f(0f, 0.707f, -0.707f), SUN_45);
        assertTrue(atSun.z >= awaySun.z);
    }

    // ===== Different turbidity produces different outputs =====

    @Test
    void hosekDifferentTurbidityDifferentOutput() {
        var low = new HosekWilkieSkyModel(1f);
        var high = new HosekWilkieSkyModel(8f);
        Vector3f rLow = low.evaluate(VIEW_ZENITH, SUN_45);
        Vector3f rHigh = high.evaluate(VIEW_ZENITH, SUN_45);
        boolean differs = rLow.x != rHigh.x || rLow.y != rHigh.y || rLow.z != rHigh.z;
        assertTrue(differs, "turbidity 1 vs 8 should differ");
    }

    @Test
    void preethamDifferentTurbidityDifferentOutput() {
        var low = new PreethamSkyModel(1f);
        var high = new PreethamSkyModel(8f);
        Vector3f rLow = low.evaluate(VIEW_ZENITH, SUN_45);
        Vector3f rHigh = high.evaluate(VIEW_ZENITH, SUN_45);
        boolean differs = rLow.x != rHigh.x || rLow.y != rHigh.y || rLow.z != rHigh.z;
        assertTrue(differs);
    }

    // ===== High turbidity increases scattering (shifts chromaticity) =====

    @Test
    void hosekHighTurbidityShiftsChromaticity() {
        var low = new HosekWilkieSkyModel(1f);
        var high = new HosekWilkieSkyModel(10f);
        Vector3f rLow = low.evaluate(VIEW_ZENITH, SUN_45);
        Vector3f rHigh = high.evaluate(VIEW_ZENITH, SUN_45);
        assertNotEquals(rLow.x, rHigh.x, "x chromaticity should shift with turbidity");
    }

    @Test
    void preethamHighTurbidityShiftsChromaticity() {
        var low = new PreethamSkyModel(1f);
        var high = new PreethamSkyModel(10f);
        Vector3f rLow = low.evaluate(VIEW_ZENITH, SUN_45);
        Vector3f rHigh = high.evaluate(VIEW_ZENITH, SUN_45);
        assertNotEquals(rLow.x, rHigh.x);
    }

    // ===== Low sun altitude produces warmer tones =====

    @Test
    void hosekLowSunProducesWarmerHorizonColor() {
        var model = new HosekWilkieSkyModel(3f);
        Vector3f lowSun = model.evaluate(VIEW_HORIZON, SUN_HORIZON);
        Vector3f highSun = model.evaluate(VIEW_ZENITH, SUN_HIGH);
        assertTrue(lowSun.x >= highSun.x, "horizon x with low sun >= zenith x with high sun");
    }

    @Test
    void preethamLowSunProducesWarmerHorizonColor() {
        var model = new PreethamSkyModel(3f);
        Vector3f lowSun = model.evaluate(VIEW_HORIZON, SUN_HORIZON);
        Vector3f highSun = model.evaluate(VIEW_ZENITH, SUN_HIGH);
        assertTrue(lowSun.x >= highSun.x);
    }

    // ===== Both models agree on qualitative behavior =====

    @Test
    void bothModelsProduceLuminanceInSameOrder() {
        var hosek = new HosekWilkieSkyModel(3f);
        var preetham = new PreethamSkyModel(3f);
        Vector3f hAtSun = hosek.evaluate(SUN_45, SUN_45);
        Vector3f hAway = hosek.evaluate(new Vector3f(0f, 0.707f, -0.707f), SUN_45);
        Vector3f pAtSun = preetham.evaluate(SUN_45, SUN_45);
        Vector3f pAway = preetham.evaluate(new Vector3f(0f, 0.707f, -0.707f), SUN_45);
        assertEquals(hAtSun.z >= hAway.z, pAtSun.z >= pAway.z);
    }

    @Test
    void bothModelsProduceValidChromaticityRanges() {
        var hosek = new HosekWilkieSkyModel(4f);
        var preetham = new PreethamSkyModel(4f);
        for (var model : new AnalyticalSkyModel[]{hosek, preetham}) {
            Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_45);
            assertTrue(xyY.x >= 0.1f && xyY.x <= 0.6f, "x in [0.1, 0.6]");
            assertTrue(xyY.y >= 0.1f && xyY.y <= 0.6f, "y in [0.1, 0.6]");
            assertTrue(xyY.x + xyY.y < 1f, "x+y < 1");
        }
    }

    // ===== Edge cases =====

    @Test
    void hosekSunAtZenithProducesValidOutput() {
        var model = new HosekWilkieSkyModel(2f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_HIGH);
        assertTrue(xyY.z > 0f);
    }

    @Test
    void preethamSunAtZenithProducesValidOutput() {
        var model = new PreethamSkyModel(2f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_HIGH);
        assertTrue(xyY.z > 0f);
    }

    @Test
    void hosekSunAtHorizonProducesValidOutput() {
        var model = new HosekWilkieSkyModel(2f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_HORIZON);
        assertTrue(xyY.z > 0f);
    }

    @Test
    void preethamSunAtHorizonProducesValidOutput() {
        var model = new PreethamSkyModel(2f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_HORIZON);
        assertTrue(xyY.z > 0f);
    }

    @Test
    void hosekVeryHighTurbidityProducesValidOutput() {
        var model = new HosekWilkieSkyModel(50f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_45);
        assertTrue(Float.isFinite(xyY.x));
        assertTrue(Float.isFinite(xyY.y));
        assertTrue(Float.isFinite(xyY.z));
        assertTrue(xyY.z > 0f);
    }

    @Test
    void preethamVeryHighTurbidityProducesValidOutput() {
        var model = new PreethamSkyModel(50f);
        Vector3f xyY = model.evaluate(VIEW_ZENITH, SUN_45);
        assertTrue(Float.isFinite(xyY.x));
        assertTrue(Float.isFinite(xyY.y));
        assertTrue(Float.isFinite(xyY.z));
        assertTrue(xyY.z > 0f);
    }

    // ===== Invalid turbidity =====

    @Test
    void hosekRejectsTurbidityBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> new HosekWilkieSkyModel(0.5f));
    }

    @Test
    void preethamRejectsTurbidityBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> new PreethamSkyModel(0.5f));
    }

    @Test
    void hosekRejectsNaNTurbidity() {
        assertThrows(IllegalArgumentException.class, () -> new HosekWilkieSkyModel(Float.NaN));
    }

    @Test
    void preethamRejectsInfinityTurbidity() {
        assertThrows(IllegalArgumentException.class, () -> new PreethamSkyModel(Float.POSITIVE_INFINITY));
    }

    // ===== evaluateLinearSrgb produces finite RGB =====

    @Test
    void hosekEvaluateLinearSrgbProducesNonNegativeComponents() {
        var model = new HosekWilkieSkyModel(3f);
        Vector3f rgb = model.evaluateLinearSrgb(VIEW_ZENITH, SUN_45, new Vector3f());
        assertTrue(rgb.x >= -0.01f, "R >= -0.01");
        assertTrue(rgb.y >= -0.01f, "G >= -0.01");
        assertTrue(rgb.z >= -0.01f, "B >= -0.01");
    }

    @Test
    void preethamEvaluateLinearSrgbProducesFiniteComponents() {
        var model = new PreethamSkyModel(3f);
        Vector3f rgb = model.evaluateLinearSrgb(VIEW_ZENITH, SUN_45, new Vector3f());
        assertTrue(Float.isFinite(rgb.x));
        assertTrue(Float.isFinite(rgb.y));
        assertTrue(Float.isFinite(rgb.z));
    }
}
