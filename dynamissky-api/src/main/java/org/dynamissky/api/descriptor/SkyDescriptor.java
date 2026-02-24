package org.dynamissky.api.descriptor;

import org.dynamissky.api.Vec3;
import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.state.WeatherState;

/**
 * Authoring descriptor for sky model and appearance selection.
 */
public record SkyDescriptor(
        SkyModelType model,
        AtmosphereConfig atmosphere,
        float turbidity,
        Vec3 groundAlbedo,
        WeatherState weather,
        String hdriPath,
        float hdriRotationDegrees) {

    public SkyDescriptor {
        if (model == null || atmosphere == null || groundAlbedo == null || weather == null) {
            throw new IllegalArgumentException("model, atmosphere, groundAlbedo, and weather are required");
        }
        if (!Float.isFinite(turbidity) || turbidity < 1f) {
            throw new IllegalArgumentException("turbidity must be finite and >= 1");
        }
        if (!Float.isFinite(hdriRotationDegrees)) {
            throw new IllegalArgumentException("hdriRotationDegrees must be finite");
        }
        if (model == SkyModelType.HDRI && (hdriPath == null || hdriPath.isBlank())) {
            throw new IllegalArgumentException("hdriPath is required for HDRI model");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SkyModelType model = SkyModelType.BRUNETON;
        private AtmosphereConfig atmosphere = AtmosphereConfig.EARTH_STANDARD;
        private float turbidity = 2.0f;
        private Vec3 groundAlbedo = new Vec3(0.1f, 0.1f, 0.1f);
        private WeatherState weather = WeatherState.CLEAR;
        private String hdriPath;
        private float hdriRotationDegrees;

        public Builder model(SkyModelType value) {
            this.model = value;
            return this;
        }

        public Builder atmosphere(AtmosphereConfig value) {
            this.atmosphere = value;
            return this;
        }

        public Builder turbidity(float value) {
            this.turbidity = value;
            return this;
        }

        public Builder groundAlbedo(Vec3 value) {
            this.groundAlbedo = value;
            return this;
        }

        public Builder weather(WeatherState value) {
            this.weather = value;
            return this;
        }

        public Builder hdriPath(String value) {
            this.hdriPath = value;
            return this;
        }

        public Builder hdriRotationDegrees(float value) {
            this.hdriRotationDegrees = value;
            return this;
        }

        public SkyDescriptor build() {
            return new SkyDescriptor(
                    model,
                    atmosphere,
                    turbidity,
                    groundAlbedo,
                    weather,
                    hdriPath,
                    hdriRotationDegrees);
        }
    }
}
