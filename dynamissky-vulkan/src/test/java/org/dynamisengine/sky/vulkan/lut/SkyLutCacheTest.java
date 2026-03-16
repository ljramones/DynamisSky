package org.dynamisengine.sky.vulkan.lut;

import org.dynamisengine.sky.api.config.AtmosphereConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SkyLutCacheTest {

    private static final AtmosphereConfig EARTH = AtmosphereConfig.EARTH_STANDARD;

    private static AtmosphereConfig tweaked(float planetRadius, float atmosphereRadius,
            float rayleighScale, float mieScale, float mieG,
            float ozoneCenter, float ozoneWidth) {
        return new AtmosphereConfig(planetRadius, atmosphereRadius,
                rayleighScale, mieScale, mieG, ozoneCenter, ozoneWidth);
    }

    @Test
    void isDirtyReturnsTrueInitially() {
        SkyLutCache cache = new SkyLutCache();
        assertTrue(cache.isDirty(EARTH));
    }

    @Test
    void isDirtyReturnsFalseAfterUpdateWithSameConfig() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertFalse(cache.isDirty(EARTH));
    }

    @Test
    void isDirtyReturnsTrueWhenConfigChanges() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(7000f, 7100f, 8f, 1.2f, 0.8f, 25f, 15f)));
    }

    @Test
    void changingPlanetRadiusTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6500f, 6600f, 8f, 1.2f, 0.8f, 25f, 15f)));
    }

    @Test
    void changingAtmosphereRadiusTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6500f, 8f, 1.2f, 0.8f, 25f, 15f)));
    }

    @Test
    void changingRayleighScaleHeightTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6460f, 10f, 1.2f, 0.8f, 25f, 15f)));
    }

    @Test
    void changingMieScaleHeightTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6460f, 8f, 2.0f, 0.8f, 25f, 15f)));
    }

    @Test
    void changingMieAnisotropyTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6460f, 8f, 1.2f, 0.5f, 25f, 15f)));
    }

    @Test
    void changingOzoneCenterTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6460f, 8f, 1.2f, 0.8f, 30f, 15f)));
    }

    @Test
    void changingOzoneWidthTriggersDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);
        assertTrue(cache.isDirty(tweaked(6360f, 6460f, 8f, 1.2f, 0.8f, 25f, 20f)));
    }

    @Test
    void identicalConfigsInSequenceDoNotTriggerDirty() {
        SkyLutCache cache = new SkyLutCache();
        cache.markClean(EARTH);

        // Same values, different object instance
        AtmosphereConfig copy = tweaked(6360f, 6460f, 8f, 1.2f, 0.8f, 25f, 15f);
        assertFalse(cache.isDirty(copy));

        cache.markClean(copy);
        assertFalse(cache.isDirty(EARTH));
    }
}
