package org.dynamissky.vulkan.integration;

import org.dynamissky.api.descriptor.SkyDescriptor;
import org.dynamissky.api.gpu.SkyGpuResources;
import org.dynamissky.api.state.MoonState;
import org.dynamissky.api.state.SunState;
import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.api.state.WeatherState;
import org.dynamissky.vulkan.SkyConfig;
import org.dynamissky.vulkan.VulkanSkyService;
import org.dynamissky.vulkan.lut.CameraState;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.vectrix.core.Matrix4f;

/**
 * DLE-facing sky integration facade.
 */
public final class VulkanSkyIntegration {
    private final VulkanSkyService skyService;
    private SkyRenderPhase phase = SkyRenderPhase.NOT_STARTED;

    private VulkanSkyIntegration(VulkanSkyService skyService) {
        this.skyService = skyService;
    }

    public static VulkanSkyIntegration create(long device,
                                              long renderPass,
                                              GpuMemoryOps memoryOps,
                                              long bindlessHeap,
                                              SkyConfig config) {
        return new VulkanSkyIntegration(VulkanSkyService.create(device, memoryOps, config));
    }

    public void update(long commandBuffer,
                       CameraState camera,
                       float deltaSeconds,
                       int frameIndex) {
        phase = SkyRenderPhase.NOT_STARTED;
        skyService.update(org.dynamissky.vulkan.SkyFrameContext.of(commandBuffer, camera, frameIndex, deltaSeconds));
        phase = SkyRenderPhase.UPDATE_COMPLETE;
    }

    public void recordBackground(long commandBuffer,
                                 Matrix4f invViewProj,
                                 int frameIndex) {
        if (phase != SkyRenderPhase.UPDATE_COMPLETE) {
            throw new AssertionError("recordBackground() called before update() this frame");
        }
        skyService.recordBackground(commandBuffer, invViewProj, frameIndex);
        phase = SkyRenderPhase.BACKGROUND_COMPLETE;
    }

    public void recordCelestial(long commandBuffer,
                                Matrix4f viewProj,
                                int frameIndex) {
        if (phase != SkyRenderPhase.BACKGROUND_COMPLETE) {
            throw new AssertionError("recordCelestial() called before recordBackground() this frame");
        }
        skyService.recordCelestial(commandBuffer, viewProj, frameIndex);
        phase = SkyRenderPhase.CELESTIAL_COMPLETE;
    }

    public SunState getSunState() {
        return skyService.sunState();
    }

    public MoonState getMoonState() {
        return skyService.moonState();
    }

    public TimeOfDayState getTimeOfDay() {
        return skyService.timeOfDayState();
    }

    public WeatherState getWeather() {
        return skyService.weatherState();
    }

    public SkyGpuResources getGpuResources() {
        return skyService.gpuResources();
    }

    public void setWeather(WeatherState weather) {
        skyService.setWeatherState(weather);
    }

    public void setSkyConfig(SkyDescriptor descriptor) {
        skyService.setSkyDescriptor(descriptor);
    }

    public void destroy() {
        skyService.destroy();
    }

    public SkyRenderPhase phase() {
        return phase;
    }

    public VulkanSkyService skyService() {
        return skyService;
    }
}
