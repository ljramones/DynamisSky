package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.sky.api.descriptor.SkyDescriptor;
import org.dynamisengine.sky.api.gpu.SkyGpuResources;
import org.dynamisengine.sky.api.state.MoonState;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.api.state.TimeOfDayState;
import org.dynamisengine.sky.api.state.WeatherState;
import org.dynamisengine.sky.vulkan.SkyConfig;
import org.dynamisengine.sky.vulkan.VulkanSkyService;
import org.dynamisengine.sky.vulkan.lut.CameraState;
import org.dynamisengine.sky.vulkan.lut.GpuMemoryOps;
import org.dynamisengine.sky.vulkan.lut.LwjglGpuMemoryOps;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.dynamisengine.vectrix.core.Matrix4f;

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

    /**
     * Convenience creation path for LightEngine runtime bridges that should avoid direct LUT class coupling.
     */
    public static VulkanSkyIntegration createWithLwjglDevices(VkDevice device,
                                                               VkPhysicalDevice physicalDevice,
                                                               long renderPass,
                                                               long bindlessHeap,
                                                               SkyConfig config) {
        GpuMemoryOps memoryOps = new LwjglGpuMemoryOps(device, physicalDevice);
        return create(device.address(), renderPass, memoryOps, bindlessHeap, config);
    }

    public void update(long commandBuffer,
                       CameraState camera,
                       float deltaSeconds,
                       int frameIndex) {
        phase = SkyRenderPhase.NOT_STARTED;
        skyService.update(org.dynamisengine.sky.vulkan.SkyFrameContext.of(commandBuffer, camera, frameIndex, deltaSeconds));
        phase = SkyRenderPhase.UPDATE_COMPLETE;
    }

    /**
     * LightEngine compatibility helper that keeps camera construction inside sky integration boundaries.
     */
    public void updateDefaultCamera(long commandBuffer,
                                    float deltaSeconds,
                                    int frameIndex) {
        update(commandBuffer, CameraState.defaultState(), deltaSeconds, frameIndex);
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
