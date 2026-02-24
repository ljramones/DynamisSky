package org.dynamissky.test.assertions;

import org.dynamissky.api.state.SunState;
import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.api.state.WeatherState;
import org.dynamissky.test.harness.TimeOfDaySimHarness;

/**
 * Domain assertions for sky simulation tests.
 */
public final class SkyAssertions {
    private SkyAssertions() {
    }

    public static void assertSunAboveHorizon(SunState sun) {
        if (sun.altitudeDegrees() <= 0f) {
            throw new AssertionError("Expected sun above horizon, altitude=" + sun.altitudeDegrees());
        }
    }

    public static void assertSunBelowHorizon(SunState sun) {
        if (sun.altitudeDegrees() >= 0f) {
            throw new AssertionError("Expected sun below horizon, altitude=" + sun.altitudeDegrees());
        }
    }

    public static void assertSunAltitudeInRange(SunState sun, float minDeg, float maxDeg) {
        if (sun.altitudeDegrees() < minDeg || sun.altitudeDegrees() > maxDeg) {
            throw new AssertionError("Sun altitude out of range: " + sun.altitudeDegrees());
        }
    }

    public static void assertSunDirectionNormalized(SunState sun) {
        float x = sun.direction().x();
        float y = sun.direction().y();
        float z = sun.direction().z();
        double len = java.lang.Math.sqrt(x * x + y * y + z * z);
        if (java.lang.Math.abs(len - 1.0) > 1e-3) {
            throw new AssertionError("Sun direction not normalized, len=" + len);
        }
    }

    public static void assertDaylight(TimeOfDayState t) {
        if (t.localTimeHours() < 6.0 || t.localTimeHours() > 18.0) {
            throw new AssertionError("Expected daylight hour, got=" + t.localTimeHours());
        }
    }

    public static void assertNight(TimeOfDayState t) {
        if (t.localTimeHours() >= 6.0 && t.localTimeHours() <= 18.0) {
            throw new AssertionError("Expected night hour, got=" + t.localTimeHours());
        }
    }

    public static void assertStarsVisible(TimeOfDayState t) {
        if (starVisibility(t) < 1.0f) {
            throw new AssertionError("Expected stars visible, hour=" + t.localTimeHours());
        }
    }

    public static void assertStarsNotVisible(TimeOfDayState t) {
        if (starVisibility(t) > 0.0f) {
            throw new AssertionError("Expected stars not visible, hour=" + t.localTimeHours());
        }
    }

    public static void assertColorTempInRange(TimeOfDayState t, float minK, float maxK) {
        if (t.colorTemperatureKelvin() < minK || t.colorTemperatureKelvin() > maxK) {
            throw new AssertionError("Color temp out of range: " + t.colorTemperatureKelvin());
        }
    }

    public static void assertRaining(WeatherState w) {
        if (w.rainIntensity() <= 0f) {
            throw new AssertionError("Expected raining state");
        }
    }

    public static void assertClear(WeatherState w) {
        if (w.rainIntensity() > 0f || w.snowIntensity() > 0f) {
            throw new AssertionError("Expected clear weather");
        }
    }

    public static void assertWindSpeedInRange(WeatherState w, float minMs, float maxMs) {
        if (w.windSpeedMetersPerSecond() < minMs || w.windSpeedMetersPerSecond() > maxMs) {
            throw new AssertionError("Wind speed out of range");
        }
    }

    public static void assertSunRisesAndSets(TimeOfDaySimHarness.SimResult result) {
        if (!result.sunRisesInWindow() || !result.sunSetsInWindow()) {
            throw new AssertionError("Expected both sunrise and sunset in window");
        }
    }

    public static void assertTimeAdvancesMonotonically(TimeOfDaySimHarness.SimResult result) {
        double last = java.lang.Double.NEGATIVE_INFINITY;
        for (TimeOfDayState t : result.timeStates()) {
            if (t.julianDate() < last) {
                throw new AssertionError("Julian date regressed");
            }
            last = t.julianDate();
        }
    }

    public static void assertColorTempPeaksAtNoon(TimeOfDaySimHarness.SimResult result) {
        int maxKelvin = Integer.MIN_VALUE;
        double maxHour = -1;
        for (TimeOfDayState t : result.timeStates()) {
            if (t.colorTemperatureKelvin() > maxKelvin) {
                maxKelvin = t.colorTemperatureKelvin();
                maxHour = t.localTimeHours();
            }
        }
        if (maxHour < 8.0 || maxHour > 14.0) {
            throw new AssertionError("Color temperature peak not near noon: hour=" + maxHour);
        }
    }

    public static float starVisibility(TimeOfDayState t) {
        double h = t.localTimeHours();
        return (h >= 6.0 && h <= 18.0) ? 0.0f : 1.0f;
    }
}
