package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SkyLutParityTest {

    @Test
    void transmittanceLutIsNonZeroAfterBake() {
        Assumptions.assumeTrue(Boolean.getBoolean("dle.sky.parity.tests"));

        FakeGpuMemoryOps memoryOps = new FakeGpuMemoryOps();
        SkyLutResources luts = SkyLutResources.create(memoryOps);
        TransmittanceLutBaker baker = TransmittanceLutBaker.create(0L, memoryOps, luts);

        baker.bake(0L, AtmosphereConfig.EARTH_STANDARD, 0);

        float[] rgba = SkyLutReadbackRegistry.readCenterPixel(luts.transmittanceLut().handle());
        assertTrue(rgba[0] > 0.0f);
        assertTrue(rgba[1] > 0.0f);
        assertTrue(rgba[2] > 0.0f);
    }

    @Test
    void multiScatteringLutIsNonZeroAfterBake() {
        Assumptions.assumeTrue(Boolean.getBoolean("dle.sky.parity.tests"));

        FakeGpuMemoryOps memoryOps = new FakeGpuMemoryOps();
        SkyLutResources luts = SkyLutResources.create(memoryOps);

        TransmittanceLutBaker transBaker = TransmittanceLutBaker.create(0L, memoryOps, luts);
        transBaker.bake(0L, AtmosphereConfig.EARTH_STANDARD, 0);

        MultiScatteringLutBaker multiBaker = MultiScatteringLutBaker.create(0L, memoryOps, luts);
        multiBaker.bake(0L, AtmosphereConfig.EARTH_STANDARD, 0);

        float[] rgba = SkyLutReadbackRegistry.readCenterPixel(luts.multiScatteringLut().handle());
        assertTrue(rgba[0] > 0.0f);
        assertTrue(rgba[1] > 0.0f);
        assertTrue(rgba[2] > 0.0f);
    }

    @Test
    void lutCachePreventsDuplicateBake() {
        SkyLutCache cache = new SkyLutCache();
        AtmosphereConfig base = AtmosphereConfig.EARTH_STANDARD;

        assertTrue(cache.isDirty(base));
        cache.markClean(base);
        assertTrue(!cache.isDirty(base));

        AtmosphereConfig modified = new AtmosphereConfig(
                base.planetRadiusKm(),
                base.atmosphereRadiusKm(),
                base.rayleighScaleHeightKm() + 0.2f,
                base.mieScaleHeightKm(),
                base.mieAnisotropyG(),
                base.ozoneLayerCenterKm(),
                base.ozoneLayerWidthKm());
        assertTrue(cache.isDirty(modified));
        cache.markClean(modified);
        assertTrue(!cache.isDirty(modified));
    }
}
