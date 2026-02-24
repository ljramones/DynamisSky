package org.dynamissky.core.scheduler;

import org.dynamissky.api.state.TimeOfDayState;

/**
 * Deterministic time-of-day progression utility.
 */
public final class TimeOfDayScheduler {
    private double julianDate;
    private double timeMultiplier;
    private final double timeZoneHours;
    private boolean locked;

    private TimeOfDayScheduler(double julianDate, double timeMultiplier, double timeZoneHours) {
        this.julianDate = julianDate;
        this.timeMultiplier = timeMultiplier;
        this.timeZoneHours = timeZoneHours;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TimeOfDayState advance(double deltaSeconds, double sunAltitudeDegrees) {
        if (!Double.isFinite(deltaSeconds) || deltaSeconds < 0d) {
            throw new IllegalArgumentException("deltaSeconds must be finite and >= 0");
        }

        if (!locked) {
            julianDate += (deltaSeconds * timeMultiplier) / 86400.0;
        }

        double localTimeHours = computeLocalTimeHours(julianDate, timeZoneHours);
        float ambient = ambientFromSunAltitude(sunAltitudeDegrees);
        int kelvin = colorTemperatureForHour(localTimeHours);

        return new TimeOfDayState(julianDate, localTimeHours, timeMultiplier, locked, ambient, kelvin);
    }

    public void setTimeMultiplier(double timeMultiplier) {
        if (!Double.isFinite(timeMultiplier) || timeMultiplier < 0d) {
            throw new IllegalArgumentException("timeMultiplier must be finite and >= 0");
        }
        this.timeMultiplier = timeMultiplier;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }

    public void setTimeOfDay(double localTimeHours) {
        if (!Double.isFinite(localTimeHours) || localTimeHours < 0d || localTimeHours > 24d) {
            throw new IllegalArgumentException("localTimeHours must be in [0,24]");
        }

        double day = java.lang.Math.floor(julianDate + 0.5) - 0.5;
        double utcHours = localTimeHours - timeZoneHours;
        while (utcHours < 0d) {
            utcHours += 24d;
            day -= 1d;
        }
        while (utcHours >= 24d) {
            utcHours -= 24d;
            day += 1d;
        }

        this.julianDate = day + utcHours / 24d;
    }

    public static float starVisibilityForSunAltitude(double sunAltitudeDegrees) {
        if (sunAltitudeDegrees <= -6d) {
            return 1f;
        }
        if (sunAltitudeDegrees >= 6d) {
            return 0f;
        }
        return (float) ((6d - sunAltitudeDegrees) / 12d);
    }

    private static double computeLocalTimeHours(double jdUtc, double zoneHours) {
        double utcHours = ((jdUtc + 0.5) - java.lang.Math.floor(jdUtc + 0.5)) * 24.0;
        double local = utcHours + zoneHours;
        while (local < 0d) {
            local += 24d;
        }
        while (local >= 24d) {
            local -= 24d;
        }
        return local;
    }

    private static float ambientFromSunAltitude(double sunAltitudeDegrees) {
        double normalized = (sunAltitudeDegrees + 6.0) / 66.0;
        double clamped = java.lang.Math.max(0.02d, java.lang.Math.min(1.0d, normalized));
        return (float) clamped;
    }

    private static int colorTemperatureForHour(double localTimeHours) {
        if (localTimeHours < 6.0) {
            return 4100;
        }
        if (localTimeHours < 8.0) {
            return lerpKelvin(localTimeHours, 6.0, 8.0, 2000, 3500);
        }
        if (localTimeHours < 14.0) {
            return lerpKelvin(localTimeHours, 8.0, 14.0, 3500, 5500);
        }
        if (localTimeHours < 19.0) {
            return lerpKelvin(localTimeHours, 14.0, 19.0, 5500, 1800);
        }
        if (localTimeHours < 21.0) {
            return lerpKelvin(localTimeHours, 19.0, 21.0, 1800, 4100);
        }
        return 4100;
    }

    private static int lerpKelvin(double t, double t0, double t1, int v0, int v1) {
        double a = (t - t0) / (t1 - t0);
        return (int) java.lang.Math.round(v0 + (v1 - v0) * a);
    }

    public static final class Builder {
        private double startJulianDate = 2451545.0;
        private double timeMultiplier = 60.0;
        private double timeZoneHours = 0.0;

        public Builder startJulianDate(double value) {
            this.startJulianDate = value;
            return this;
        }

        public Builder timeMultiplier(double value) {
            this.timeMultiplier = value;
            return this;
        }

        public Builder timeZone(double value) {
            this.timeZoneHours = value;
            return this;
        }

        public TimeOfDayScheduler build() {
            return new TimeOfDayScheduler(startJulianDate, timeMultiplier, timeZoneHours);
        }
    }
}
