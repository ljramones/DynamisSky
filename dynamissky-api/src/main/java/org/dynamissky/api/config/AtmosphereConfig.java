package org.dynamissky.api.config;

/**
 * Physical atmosphere parameters used by analytical and LUT-based models.
 */
public record AtmosphereConfig(
        float planetRadiusKm,
        float atmosphereRadiusKm,
        float rayleighScaleHeightKm,
        float mieScaleHeightKm,
        float mieAnisotropyG,
        float ozoneLayerCenterKm,
        float ozoneLayerWidthKm) {

    public static final AtmosphereConfig EARTH_STANDARD = new AtmosphereConfig(
            6360f,
            6460f,
            8f,
            1.2f,
            0.8f,
            25f,
            15f);

    public AtmosphereConfig {
        if (!Float.isFinite(planetRadiusKm) || planetRadiusKm <= 0f) {
            throw new IllegalArgumentException("planetRadiusKm must be finite and > 0");
        }
        if (!Float.isFinite(atmosphereRadiusKm) || atmosphereRadiusKm <= planetRadiusKm) {
            throw new IllegalArgumentException("atmosphereRadiusKm must be > planetRadiusKm");
        }
        if (!Float.isFinite(rayleighScaleHeightKm) || rayleighScaleHeightKm <= 0f) {
            throw new IllegalArgumentException("rayleighScaleHeightKm must be finite and > 0");
        }
        if (!Float.isFinite(mieScaleHeightKm) || mieScaleHeightKm <= 0f) {
            throw new IllegalArgumentException("mieScaleHeightKm must be finite and > 0");
        }
        if (!Float.isFinite(mieAnisotropyG) || mieAnisotropyG < -1f || mieAnisotropyG > 1f) {
            throw new IllegalArgumentException("mieAnisotropyG must be in [-1,1]");
        }
        if (!Float.isFinite(ozoneLayerCenterKm) || ozoneLayerCenterKm <= 0f) {
            throw new IllegalArgumentException("ozoneLayerCenterKm must be finite and > 0");
        }
        if (!Float.isFinite(ozoneLayerWidthKm) || ozoneLayerWidthKm <= 0f) {
            throw new IllegalArgumentException("ozoneLayerWidthKm must be finite and > 0");
        }
    }
}
