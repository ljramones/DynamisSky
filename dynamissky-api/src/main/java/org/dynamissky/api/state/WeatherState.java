package org.dynamissky.api.state;

import org.dynamissky.api.Vec3;

/**
 * Weather descriptor shared across engine systems.
 */
public record WeatherState(
        float windSpeedMetersPerSecond,
        Vec3 windDirection,
        float rainIntensity,
        float snowIntensity,
        float fogDensity,
        float cloudCoverage) {

    public static final WeatherState CLEAR = builder().build();
    public static final WeatherState HEAVY_RAIN = builder()
            .windSpeedMetersPerSecond(12.0f)
            .windDirection(new Vec3(0.7f, 0f, 0.7f))
            .rainIntensity(1.0f)
            .fogDensity(0.4f)
            .cloudCoverage(1.0f)
            .build();

    public WeatherState {
        if (windDirection == null) {
            throw new IllegalArgumentException("windDirection is required");
        }
        checkUnitRange(rainIntensity, "rainIntensity");
        checkUnitRange(snowIntensity, "snowIntensity");
        checkUnitRange(fogDensity, "fogDensity");
        checkUnitRange(cloudCoverage, "cloudCoverage");
        if (!Float.isFinite(windSpeedMetersPerSecond) || windSpeedMetersPerSecond < 0f) {
            throw new IllegalArgumentException("windSpeedMetersPerSecond must be finite and >= 0");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static void checkUnitRange(float value, String name) {
        if (!Float.isFinite(value) || value < 0f || value > 1f) {
            throw new IllegalArgumentException(name + " must be in [0,1]");
        }
    }

    public static final class Builder {
        private float windSpeedMetersPerSecond;
        private Vec3 windDirection = Vec3.ZERO;
        private float rainIntensity;
        private float snowIntensity;
        private float fogDensity;
        private float cloudCoverage;

        public Builder windSpeedMetersPerSecond(float value) {
            this.windSpeedMetersPerSecond = value;
            return this;
        }

        public Builder windDirection(Vec3 value) {
            this.windDirection = value;
            return this;
        }

        public Builder rainIntensity(float value) {
            this.rainIntensity = value;
            return this;
        }

        public Builder snowIntensity(float value) {
            this.snowIntensity = value;
            return this;
        }

        public Builder fogDensity(float value) {
            this.fogDensity = value;
            return this;
        }

        public Builder cloudCoverage(float value) {
            this.cloudCoverage = value;
            return this;
        }

        public WeatherState build() {
            return new WeatherState(
                    windSpeedMetersPerSecond,
                    windDirection,
                    rainIntensity,
                    snowIntensity,
                    fogDensity,
                    cloudCoverage);
        }
    }
}
