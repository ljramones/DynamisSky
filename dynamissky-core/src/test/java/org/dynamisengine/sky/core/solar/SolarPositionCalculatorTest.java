package org.dynamisengine.sky.core.solar;

import org.junit.jupiter.api.Test;
import org.vectrix.core.Vector3f;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolarPositionCalculatorTest {

    private static final double TOLERANCE = 2.0;

    // ---- 1. Noon sun at equator on spring equinox — altitude near 90° ----
    @Test
    void noonEquatorEquinox_altitudeNear90() {
        // March 20 2024 12:00 UTC, equator at prime meridian
        JulianDate jd = JulianDate.of(2024, 3, 20, 12, 0, 0);
        LatLon equator = new LatLon(0.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, equator, new TimeZoneOffset(0f));
        assertEquals(90.0, pos.altitudeDegrees(), 2.5,
                "Sun should be nearly overhead at equator on equinox noon");
    }

    // ---- 2. Midnight sun at equator — altitude negative ----
    @Test
    void midnightEquator_altitudeNegative() {
        JulianDate jd = JulianDate.of(2024, 6, 21, 0, 0, 0);
        LatLon equator = new LatLon(0.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, equator, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() < 0,
                "Sun should be below horizon at midnight: " + pos.altitudeDegrees());
    }

    // ---- 3. Summer solstice at Tropic of Cancer — sun nearly overhead ----
    @Test
    void summerSolsticeTropicOfCancer_sunNearlyOverhead() {
        // June 21 2024 12:00 UTC at 23.44°N, 0°E
        JulianDate jd = JulianDate.of(2024, 6, 21, 12, 0, 0);
        LatLon tropic = new LatLon(23.44, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, tropic, new TimeZoneOffset(0f));
        assertEquals(90.0, pos.altitudeDegrees(), 2.5,
                "Sun should be near zenith at Tropic of Cancer on summer solstice");
    }

    // ---- 4. Winter at high latitude — sun stays low ----
    @Test
    void winterHighLatitude_sunStaysLow() {
        // Dec 21 2024 12:00 UTC at 65°N (Fairbanks-ish), 0°E
        JulianDate jd = JulianDate.of(2024, 12, 21, 12, 0, 0);
        LatLon high = new LatLon(65.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, high, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() < 5.0,
                "Sun should be very low at 65°N in winter: " + pos.altitudeDegrees());
    }

    // ---- 5. NOAA reference: Denver, July 4 2024 18:00 UTC (noon MDT) ----
    @Test
    void noaaReference_denverJuly4() {
        // Denver: 39.7392°N, 104.9903°W. July 4 2024 18:00 UTC = noon MDT
        // Denver 39.74°N, 105°W on July 4, morning (14:00 UTC = 8 AM MDT)
        // Sun should be in eastern sky (azimuth < 180) at moderate altitude
        JulianDate jd = JulianDate.of(2024, 7, 4, 14, 0, 0);
        LatLon denver = new LatLon(39.7392, -104.9903);
        SolarPosition pos = SolarPositionCalculator.compute(jd, denver, new TimeZoneOffset(-7f));
        assertTrue(pos.altitudeDegrees() > 20.0 && pos.altitudeDegrees() < 60.0,
                "Altitude should be moderate in the morning: " + pos.altitudeDegrees());
        assertTrue(pos.azimuthDegrees() > 45.0 && pos.azimuthDegrees() < 135.0,
                "Azimuth should be in the east in the morning: " + pos.azimuthDegrees());
    }

    // ---- 6. Sunrise — altitude near 0° around 6 AM solar time ----
    @Test
    void sunrise_altitudeNearZero() {
        // Equator, equinox, ~6:00 UTC — sunrise
        JulianDate jd = JulianDate.of(2024, 3, 20, 6, 0, 0);
        LatLon equator = new LatLon(0.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, equator, new TimeZoneOffset(0f));
        assertEquals(0.0, pos.altitudeDegrees(), 5.0,
                "Altitude should be near 0° at sunrise: " + pos.altitudeDegrees());
    }

    // ---- 6b. Sunset — altitude near 0° around 18:00 solar time ----
    @Test
    void sunset_altitudeNearZero() {
        JulianDate jd = JulianDate.of(2024, 3, 20, 18, 0, 0);
        LatLon equator = new LatLon(0.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, equator, new TimeZoneOffset(0f));
        assertEquals(0.0, pos.altitudeDegrees(), 5.0,
                "Altitude should be near 0° at sunset: " + pos.altitudeDegrees());
    }

    // ---- 7. Southern hemisphere — inverted season ----
    @Test
    void southernHemisphere_invertedSeason() {
        // Dec 21 is summer in southern hemisphere
        // Tropic of Capricorn 23.44°S at noon UTC
        JulianDate jd = JulianDate.of(2024, 12, 21, 12, 0, 0);
        LatLon tropicCapricorn = new LatLon(-23.44, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, tropicCapricorn, new TimeZoneOffset(0f));
        assertEquals(90.0, pos.altitudeDegrees(), 2.5,
                "Sun should be near zenith at Tropic of Capricorn on December solstice");
    }

    @Test
    void southernHemisphere_juneSunLow() {
        // June 21 at 50°S — winter, sun should be low at noon
        JulianDate jd = JulianDate.of(2024, 6, 21, 12, 0, 0);
        LatLon south50 = new LatLon(-50.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, south50, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() < 20.0,
                "Sun should be low at 50°S in June: " + pos.altitudeDegrees());
    }

    // ---- 8. toWorldDirection — unit vector, Y matches altitude sign ----
    @Test
    void toWorldDirection_unitVector() {
        SolarPosition pos = new SolarPosition(180.0, 45.0);
        Vector3f north = new Vector3f(0f, 0f, -1f);
        Vector3f dir = pos.toWorldDirection(north);
        assertEquals(1.0f, dir.length(), 0.01f, "Direction should be unit length");
    }

    @Test
    void toWorldDirection_positiveAltitude_positiveY() {
        SolarPosition pos = new SolarPosition(180.0, 30.0);
        Vector3f dir = pos.toWorldDirection(new Vector3f(0f, 0f, -1f));
        assertTrue(dir.y > 0, "Y should be positive for positive altitude: " + dir.y);
    }

    @Test
    void toWorldDirection_negativeAltitude_negativeY() {
        SolarPosition pos = new SolarPosition(180.0, -15.0);
        Vector3f dir = pos.toWorldDirection(new Vector3f(0f, 0f, -1f));
        assertTrue(dir.y < 0, "Y should be negative for negative altitude: " + dir.y);
    }

    @Test
    void toWorldDirection_altitude90_pointsUp() {
        SolarPosition pos = new SolarPosition(0.0, 90.0);
        Vector3f dir = pos.toWorldDirection(new Vector3f(0f, 0f, -1f));
        assertEquals(1.0f, dir.y, 0.01f, "Zenith direction should point straight up");
        assertEquals(0.0f, dir.x, 0.01f);
        assertEquals(0.0f, dir.z, 0.01f);
    }

    // ---- 9. toWorldDirection — azimuth rotation ----
    @Test
    void toWorldDirection_azimuth0_pointsNorth() {
        // Azimuth 0 = north, altitude 0 = horizon
        SolarPosition pos = new SolarPosition(0.0, 0.0);
        Vector3f north = new Vector3f(0f, 0f, -1f);
        Vector3f dir = pos.toWorldDirection(north);
        // Should point in the north direction (0, 0, -1)
        assertEquals(0.0f, dir.y, 0.05f, "On horizon");
        assertTrue(dir.z < -0.5f, "Should point north (negative Z): " + dir.z);
    }

    @Test
    void toWorldDirection_azimuth180_pointsSouth() {
        SolarPosition pos = new SolarPosition(180.0, 0.0);
        Vector3f dir = pos.toWorldDirection(new Vector3f(0f, 0f, -1f));
        assertEquals(0.0f, dir.y, 0.05f);
        assertTrue(dir.z > 0.5f, "Should point south (positive Z): " + dir.z);
    }

    @Test
    void toWorldDirection_azimuth90_pointsEast() {
        SolarPosition pos = new SolarPosition(90.0, 0.0);
        Vector3f dir = pos.toWorldDirection(new Vector3f(0f, 0f, -1f));
        assertEquals(0.0f, dir.y, 0.05f);
        assertTrue(dir.x > 0.5f, "Should point east (positive X): " + dir.x);
    }

    @Test
    void toWorldDirection_nullNorthThrows() {
        SolarPosition pos = new SolarPosition(0.0, 0.0);
        try {
            pos.toWorldDirection(null);
            assertTrue(false, "Should have thrown");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    // ---- 10. Edge cases: polar regions, date line ----
    @Test
    void northPole_summerSolstice_midnightSun() {
        // At 89°N on June 21, sun should be above horizon even at midnight
        JulianDate jd = JulianDate.of(2024, 6, 21, 0, 0, 0);
        LatLon nearPole = new LatLon(89.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, nearPole, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() > 0,
                "Midnight sun at 89°N on summer solstice: " + pos.altitudeDegrees());
    }

    @Test
    void northPole_winterSolstice_polarNight() {
        // At 89°N on Dec 21 noon, sun should be below horizon
        JulianDate jd = JulianDate.of(2024, 12, 21, 12, 0, 0);
        LatLon nearPole = new LatLon(89.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, nearPole, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() < 0,
                "Polar night at 89°N on winter solstice: " + pos.altitudeDegrees());
    }

    @Test
    void dateLine_eastWest_consistentResults() {
        // Compare positions just east and west of the date line
        JulianDate jd = JulianDate.of(2024, 6, 21, 0, 0, 0);
        LatLon east = new LatLon(0.0, 179.0);
        LatLon west = new LatLon(0.0, -179.0);
        SolarPosition posEast = SolarPositionCalculator.compute(jd, east, new TimeZoneOffset(0f));
        SolarPosition posWest = SolarPositionCalculator.compute(jd, west, new TimeZoneOffset(0f));
        // Altitude should be very close (only 2° longitude apart effectively)
        assertEquals(posEast.altitudeDegrees(), posWest.altitudeDegrees(), 3.0,
                "Altitude should be similar across date line");
    }

    @Test
    void southPole_decemberSolstice_midnightSun() {
        JulianDate jd = JulianDate.of(2024, 12, 21, 0, 0, 0);
        LatLon nearSouthPole = new LatLon(-89.0, 0.0);
        SolarPosition pos = SolarPositionCalculator.compute(jd, nearSouthPole, new TimeZoneOffset(0f));
        assertTrue(pos.altitudeDegrees() > 0,
                "Midnight sun at south pole in December: " + pos.altitudeDegrees());
    }

    // ---- Three-arg compute delegates correctly ----
    @Test
    void threeArgCompute_matchesTwoArg() {
        JulianDate jd = JulianDate.of(2024, 6, 21, 12, 0, 0);
        LatLon loc = new LatLon(40.0, -74.0);
        SolarPosition pos3 = SolarPositionCalculator.compute(jd, loc, new TimeZoneOffset(-5f));
        SolarPosition pos2 = SolarPositionCalculator.compute(jd.value(), loc);
        assertEquals(pos2.altitudeDegrees(), pos3.altitudeDegrees(), 1e-9);
        assertEquals(pos2.azimuthDegrees(), pos3.azimuthDegrees(), 1e-9);
    }
}
