package org.dynamissky.api.state;

/**
 * Time progression and ambient state derived from scheduler and sun model.
 */
public record TimeOfDayState(
        double julianDate,
        double localTimeHours,
        double timeMultiplier,
        boolean locked,
        float ambientIntensity,
        int colorTemperatureKelvin) {

    public TimeOfDayState {
        if (!Double.isFinite(julianDate)) {
            throw new IllegalArgumentException("julianDate must be finite");
        }
        if (!Double.isFinite(localTimeHours) || localTimeHours < 0d || localTimeHours > 24d) {
            throw new IllegalArgumentException("localTimeHours must be in [0,24]");
        }
        if (!Double.isFinite(timeMultiplier) || timeMultiplier < 0d) {
            throw new IllegalArgumentException("timeMultiplier must be finite and >= 0");
        }
        if (!Float.isFinite(ambientIntensity) || ambientIntensity < 0f) {
            throw new IllegalArgumentException("ambientIntensity must be finite and >= 0");
        }
        if (colorTemperatureKelvin <= 0) {
            throw new IllegalArgumentException("colorTemperatureKelvin must be > 0");
        }
    }
}
