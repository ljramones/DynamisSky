package org.dynamissky.vulkan;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.descriptor.SkyModelType;

/**
 * Vulkan sky service bootstrap configuration.
 */
public record SkyConfig(
        SkyModelType model,
        AtmosphereConfig atmosphere,
        double latitudeDegrees,
        double longitudeDegrees,
        double startJulianDate,
        double timeZoneHours,
        double timeMultiplier) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SkyModelType model = SkyModelType.BRUNETON;
        private AtmosphereConfig atmosphere = AtmosphereConfig.EARTH_STANDARD;
        private double latitudeDegrees = 43.7;
        private double longitudeDegrees = -79.4;
        private double startJulianDate = 2460310.5;
        private double timeZoneHours = 0.0;
        private double timeMultiplier = 60.0;

        public Builder model(SkyModelType value) {
            this.model = value;
            return this;
        }

        public Builder atmosphere(AtmosphereConfig value) {
            this.atmosphere = value;
            return this;
        }

        public Builder latitude(double value) {
            this.latitudeDegrees = value;
            return this;
        }

        public Builder longitude(double value) {
            this.longitudeDegrees = value;
            return this;
        }

        public Builder startJulianDate(double value) {
            this.startJulianDate = value;
            return this;
        }

        public Builder timeZone(double value) {
            this.timeZoneHours = value;
            return this;
        }

        public Builder timeMultiplier(double value) {
            this.timeMultiplier = value;
            return this;
        }

        public SkyConfig build() {
            return new SkyConfig(model, atmosphere, latitudeDegrees, longitudeDegrees, startJulianDate, timeZoneHours, timeMultiplier);
        }
    }
}
