package org.dynamissky.vulkan.lut;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.vulkan.SkyConfig;
import org.dynamissky.vulkan.SkyFrameContext;
import org.dynamissky.vulkan.VulkanSkyService;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void skyViewLutIsNonBlackAtZenith() {
        Assumptions.assumeTrue(Boolean.getBoolean("dle.sky.parity.tests"));

        VulkanSkyService service = VulkanSkyService.create(0L, new FakeGpuMemoryOps(), SkyConfig.builder().build());
        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 0, 1f));

        float[] rgba = SkyLutReadbackRegistry.readCenterPixel(service.gpuResources().skyViewLut().handle());
        assertTrue(rgba[0] + rgba[1] + rgba[2] > 0.1f);
    }

    @Test
    void aerialPerspectiveLutIsNonZero() {
        Assumptions.assumeTrue(Boolean.getBoolean("dle.sky.parity.tests"));

        VulkanSkyService service = VulkanSkyService.create(0L, new FakeGpuMemoryOps(), SkyConfig.builder().build());
        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 0, 1f));

        float[] rgba = SkyLutReadbackRegistry.readCenterPixel(service.gpuResources().aerialPerspectiveLut().handle());
        assertTrue(rgba[3] > 0.0f);
        assertTrue(rgba[3] <= 1.0f);
    }

    @Test
    void updateRunsBakeOnFirstCall() {
        VulkanSkyService service = VulkanSkyService.create(0L, new FakeGpuMemoryOps(), SkyConfig.builder().build());
        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 0, 1f));

        assertFalse(service.isLutCacheDirty());
    }

    @Test
    void updateSkipsBakeOnSubsequentCalls() {
        VulkanSkyService service = VulkanSkyService.create(0L, new FakeGpuMemoryOps(), SkyConfig.builder().build());

        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 0, 1f));
        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 1, 1f));
        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 2, 1f));

        assertEquals(1, service.bakeDispatchCount());
    }
}
