package org.dynamissky.vulkan.integration;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.descriptor.SkyDescriptor;
import org.dynamissky.vulkan.SkyConfig;
import org.dynamissky.vulkan.lut.CameraState;
import org.dynamissky.vulkan.lut.GpuImage2DAlloc;
import org.dynamissky.vulkan.lut.GpuImage3DAlloc;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.junit.jupiter.api.Test;
import org.vectrix.core.Matrix4f;
import org.vectrix.core.Vector3f;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkyIntegrationParityTest {

    @Test
    void integrationCreatesWithoutError() {
        VulkanSkyIntegration integration = VulkanSkyIntegration.create(1L, 2L, new FakeGpuMemoryOps(), 0L,
                SkyConfig.builder().build());
        assertNotNull(integration);
        assertNotNull(integration.getGpuResources());
    }

    @Test
    void updatePopulatesSunState() {
        VulkanSkyIntegration integration = noonIntegration();
        integration.update(0L, CameraState.defaultState(), 1f, 0);

        var sun = integration.getSunState();
        float len = (float) java.lang.Math.sqrt(
                sun.direction().x() * sun.direction().x()
                        + sun.direction().y() * sun.direction().y()
                        + sun.direction().z() * sun.direction().z());

        assertTrue(sun.altitudeDegrees() > 0f);
        assertTrue(sun.intensity() > 0f);
        assertTrue(java.lang.Math.abs(len - 1f) < 1e-3f);
    }

    @Test
    void updatePopulatesTimeOfDay() {
        VulkanSkyIntegration integration = noonIntegration();
        integration.update(0L, CameraState.defaultState(), 1f, 0);

        var tod = integration.getTimeOfDay();
        assertTrue(tod.colorTemperatureKelvin() >= 1000 && tod.colorTemperatureKelvin() <= 10000);
        assertTrue(tod.ambientIntensity() >= 0f);
    }

    @Test
    void sunLightAdapterProducesNonZeroDirection() {
        VulkanSkyIntegration integration = noonIntegration();
        integration.update(0L, CameraState.defaultState(), 1f, 0);

        DirectionalLightData light = VulkanSkySunLightAdapter.adapt(integration.getSunState());
        float len = light.direction().length();
        assertTrue(len > 0f);
        assertTrue(light.color().x() > 0f);
        assertTrue(light.color().y() > 0f);
        assertTrue(light.color().z() > 0f);
    }

    @Test
    void ambientAdapterProducesValidData() {
        VulkanSkyIntegration integration = noonIntegration();
        integration.update(0L, CameraState.defaultState(), 1f, 0);

        AmbientLightData ambient = VulkanSkyAmbientAdapter.adapt(integration.getTimeOfDay());
        assertTrue(ambient.intensity() > 0f);
        assertTrue(ambient.color().x() > 0f);
        assertTrue(ambient.color().y() > 0f);
        assertTrue(ambient.color().z() > 0f);
    }

    @Test
    void phaseOrderingEnforcedByAssertions() {
        VulkanSkyIntegration integration = noonIntegration();

        assertThrows(AssertionError.class,
                () -> integration.recordBackground(0L, new Matrix4f().identity(), 0));
        assertThrows(AssertionError.class,
                () -> integration.recordCelestial(0L, new Matrix4f().identity(), 0));

        integration.update(0L, CameraState.defaultState(), 1f, 0);
        integration.recordBackground(0L, new Matrix4f().identity(), 0);
        integration.recordCelestial(0L, new Matrix4f().identity(), 0);
    }

    @Test
    void setSkyDescriptorMarksCacheDirtyOnAtmosphereChange() {
        VulkanSkyIntegration integration = noonIntegration();
        integration.update(0L, CameraState.defaultState(), 1f, 0);

        assertTrue(!integration.skyService().isLutCacheDirty());

        AtmosphereConfig base = integration.skyService().atmosphereConfig();
        AtmosphereConfig changed = new AtmosphereConfig(
                base.planetRadiusKm(),
                base.atmosphereRadiusKm(),
                base.rayleighScaleHeightKm() + 0.3f,
                base.mieScaleHeightKm(),
                base.mieAnisotropyG(),
                base.ozoneLayerCenterKm(),
                base.ozoneLayerWidthKm());

        SkyDescriptor descriptor = SkyDescriptor.builder()
                .model(integration.skyService().skyDescriptor().model())
                .atmosphere(changed)
                .weather(integration.skyService().weatherState())
                .build();

        integration.setSkyConfig(descriptor);

        assertTrue(integration.skyService().isLutCacheDirty());
    }

    private static VulkanSkyIntegration noonIntegration() {
        return VulkanSkyIntegration.create(
                1L,
                2L,
                new FakeGpuMemoryOps(),
                0L,
                SkyConfig.builder()
                        .latitude(0.0)
                        .longitude(0.0)
                        .startJulianDate(2451545.0)
                        .timeZone(0.0)
                        .build());
    }

    private static final class FakeGpuMemoryOps implements GpuMemoryOps {
        private long handle = 1;

        @Override
        public GpuImage2DAlloc createImage2D(int width, int height, int format) {
            long image = handle++;
            long memory = handle++;
            return new GpuImage2DAlloc(new org.dynamissky.api.gpu.GpuImage2D(image, width, height), memory, format);
        }

        @Override
        public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
            long image = handle++;
            long memory = handle++;
            return new GpuImage3DAlloc(new org.dynamissky.api.gpu.GpuImage3D(image, width, height, depth), memory, format);
        }

        @Override
        public void destroyImage(long imageHandle, long memoryHandle) {
            // no-op
        }
    }
}
