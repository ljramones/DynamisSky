package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;

import java.util.Objects;

/**
 * Tracks last baked atmosphere config to avoid duplicate LUT rebakes.
 */
public final class SkyLutCache {
    private int lastConfigHash;
    private boolean initialized;

    public boolean isDirty(AtmosphereConfig current) {
        int hash = hashOf(current);
        return !initialized || hash != lastConfigHash;
    }

    public void markClean(AtmosphereConfig config) {
        this.lastConfigHash = hashOf(config);
        this.initialized = true;
    }

    private static int hashOf(AtmosphereConfig config) {
        return Objects.hash(
                config.planetRadiusKm(),
                config.atmosphereRadiusKm(),
                config.rayleighScaleHeightKm(),
                config.mieScaleHeightKm(),
                config.mieAnisotropyG(),
                config.ozoneLayerCenterKm(),
                config.ozoneLayerWidthKm());
    }
}
