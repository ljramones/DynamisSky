package org.dynamissky.vulkan;

import org.dynamissky.api.ColorRgb;
import org.dynamissky.api.Vec3;
import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.descriptor.SkyDescriptor;
import org.dynamissky.api.descriptor.SkyModelType;
import org.dynamissky.api.gpu.SkyGpuResources;
import org.dynamissky.api.service.SkyService;
import org.dynamissky.api.state.MoonState;
import org.dynamissky.api.state.SunState;
import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.api.state.WeatherState;
import org.dynamissky.core.SkyTypeAdapters;
import org.dynamissky.core.color.KelvinToRgb;
import org.dynamissky.core.scheduler.TimeOfDayScheduler;
import org.dynamissky.core.solar.JulianDate;
import org.dynamissky.core.solar.LatLon;
import org.dynamissky.core.solar.SolarPosition;
import org.dynamissky.core.solar.SolarPositionCalculator;
import org.dynamissky.vulkan.lut.AerialPerspectiveLutUpdater;
import org.dynamissky.vulkan.lut.CameraState;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.dynamissky.vulkan.lut.MultiScatteringLutBaker;
import org.dynamissky.vulkan.lut.SkyLutCache;
import org.dynamissky.vulkan.lut.SkyLutResources;
import org.dynamissky.vulkan.lut.SkyViewLutUpdater;
import org.dynamissky.vulkan.lut.TransmittanceLutBaker;
import org.vectrix.core.Vector3f;

/**
 * Top-level Vulkan-backed sky service coordinating LUT bake and per-frame updates.
 */
public final class VulkanSkyService implements SkyService {
    private final SkyLutResources lutResources;
    private final TransmittanceLutBaker transmittanceBaker;
    private final MultiScatteringLutBaker multiScatterBaker;
    private final SkyViewLutUpdater skyViewUpdater;
    private final AerialPerspectiveLutUpdater aerialUpdater;
    private final SkyLutCache lutCache;
    private final TimeOfDayScheduler scheduler;

    private final LatLon latLon;

    private AtmosphereConfig atmosphereConfig;
    private WeatherState weatherState;
    private SkyDescriptor skyDescriptor;

    private SunState sunState;
    private MoonState moonState;
    private TimeOfDayState timeOfDayState;
    private int bakeDispatchCount;

    private VulkanSkyService(
            SkyLutResources lutResources,
            TransmittanceLutBaker transmittanceBaker,
            MultiScatteringLutBaker multiScatterBaker,
            SkyViewLutUpdater skyViewUpdater,
            AerialPerspectiveLutUpdater aerialUpdater,
            SkyLutCache lutCache,
            TimeOfDayScheduler scheduler,
            LatLon latLon,
            SkyConfig config) {
        this.lutResources = lutResources;
        this.transmittanceBaker = transmittanceBaker;
        this.multiScatterBaker = multiScatterBaker;
        this.skyViewUpdater = skyViewUpdater;
        this.aerialUpdater = aerialUpdater;
        this.lutCache = lutCache;
        this.scheduler = scheduler;
        this.latLon = latLon;

        this.atmosphereConfig = config.atmosphere();
        this.weatherState = WeatherState.CLEAR;
        this.skyDescriptor = SkyDescriptor.builder()
                .model(config.model())
                .atmosphere(config.atmosphere())
                .weather(weatherState)
                .build();

        this.sunState = new SunState(new Vec3(0f, 1f, 0f), new ColorRgb(1f, 1f, 1f), 1f, 180d, 45d);
        this.moonState = new MoonState(new Vec3(0f, -1f, 0f), new ColorRgb(0.4f, 0.45f, 0.5f), 0.1f, 0.5f, 0.25f);
        this.timeOfDayState = new TimeOfDayState(config.startJulianDate(), 12d, config.timeMultiplier(), false, 1f, 5500);
    }

    public static VulkanSkyService create(long device, GpuMemoryOps memoryOps, SkyConfig config) {
        SkyLutResources luts = SkyLutResources.create(memoryOps);
        return new VulkanSkyService(
                luts,
                TransmittanceLutBaker.create(device, memoryOps, luts),
                MultiScatteringLutBaker.create(device, memoryOps, luts),
                SkyViewLutUpdater.create(device, memoryOps, luts),
                AerialPerspectiveLutUpdater.create(device, memoryOps, luts),
                new SkyLutCache(),
                TimeOfDayScheduler.builder()
                        .startJulianDate(config.startJulianDate())
                        .timeZone(config.timeZoneHours())
                        .timeMultiplier(config.timeMultiplier())
                        .build(),
                new LatLon(config.latitudeDegrees(), config.longitudeDegrees()),
                config);
    }

    public void update(SkyFrameContext ctx) {
        SolarPosition solar = SolarPositionCalculator.compute(
                new JulianDate(timeOfDayState.julianDate()),
                latLon,
                new org.dynamissky.core.solar.TimeZoneOffset((float) 0));

        timeOfDayState = scheduler.advance(ctx.deltaSeconds(), solar.altitudeDegrees());
        solar = SolarPositionCalculator.compute(timeOfDayState.julianDate(), latLon);

        Vector3f sunDir = solar.toWorldDirection(new Vector3f(0f, 0f, -1f));
        Vector3f sunColorLinear = KelvinToRgb.toLinearRgb(timeOfDayState.colorTemperatureKelvin(), new Vector3f());
        float sunIntensity = (float) java.lang.Math.max(0.05, java.lang.Math.sin(java.lang.Math.toRadians(solar.altitudeDegrees())) + 0.1);

        sunState = new SunState(
                SkyTypeAdapters.toApiVec3(sunDir),
                SkyTypeAdapters.toApiColor(sunColorLinear),
                sunIntensity,
                solar.azimuthDegrees(),
                solar.altitudeDegrees());

        Vector3f moonDir = new Vector3f(sunDir).negate().normalize();
        moonState = new MoonState(
                SkyTypeAdapters.toApiVec3(moonDir),
                new ColorRgb(0.4f, 0.44f, 0.5f),
                0.1f,
                0.5f,
                0.25f);

        if (lutCache.isDirty(atmosphereConfig)) {
            transmittanceBaker.bake(ctx.commandBuffer(), atmosphereConfig, ctx.frameIndex());
            multiScatterBaker.bake(ctx.commandBuffer(), atmosphereConfig, ctx.frameIndex());
            lutCache.markClean(atmosphereConfig);
            bakeDispatchCount++;
            transitionToShaderRead(lutResources.transmittanceLut().handle());
            transitionToShaderRead(lutResources.multiScatteringLut().handle());
        }

        skyViewUpdater.update(ctx.commandBuffer(), sunState, atmosphereConfig, ctx.frameIndex());
        transitionToShaderRead(lutResources.skyViewLut().handle());

        CameraState camera = ctx.camera() != null ? ctx.camera() : CameraState.defaultState();
        aerialUpdater.update(ctx.commandBuffer(), sunState, atmosphereConfig, camera, ctx.frameIndex());
        transitionToShaderRead(lutResources.aerialPerspectiveLut().handle());
    }

    private void transitionToShaderRead(long imageHandle) {
        // Barrier chain integration happens in renderer command recorder phase.
    }

    public boolean isLutCacheDirty() {
        return lutCache.isDirty(atmosphereConfig);
    }

    public int bakeDispatchCount() {
        return bakeDispatchCount;
    }

    public void destroy() {
        skyViewUpdater.destroy();
        aerialUpdater.destroy();
        transmittanceBaker.destroy();
        multiScatterBaker.destroy();
        lutResources.destroy();
    }

    @Override
    public SunState sunState() {
        return sunState;
    }

    @Override
    public MoonState moonState() {
        return moonState;
    }

    @Override
    public TimeOfDayState timeOfDayState() {
        return timeOfDayState;
    }

    @Override
    public WeatherState weatherState() {
        return weatherState;
    }

    @Override
    public AtmosphereConfig atmosphereConfig() {
        return atmosphereConfig;
    }

    @Override
    public SkyDescriptor skyDescriptor() {
        return skyDescriptor;
    }

    @Override
    public SkyGpuResources gpuResources() {
        return new SkyGpuResources(
                lutResources.transmittanceLut(),
                lutResources.multiScatteringLut(),
                lutResources.skyViewLut(),
                lutResources.aerialPerspectiveLut(),
                0L);
    }

    @Override
    public void applySkyDescriptor(SkyDescriptor descriptor) {
        this.skyDescriptor = descriptor;
        this.atmosphereConfig = descriptor.atmosphere();
        this.weatherState = descriptor.weather();
    }

    @Override
    public void setWeatherState(WeatherState weatherState) {
        this.weatherState = weatherState;
    }

    @Override
    public void setTimeMultiplier(double timeMultiplier) {
        scheduler.setTimeMultiplier(timeMultiplier);
    }

    @Override
    public void setTimeOfDayHours(double localTimeHours) {
        scheduler.setTimeOfDay(localTimeHours);
    }

    @Override
    public void lockTimeOfDay() {
        scheduler.lock();
    }

    @Override
    public void unlockTimeOfDay() {
        scheduler.unlock();
    }
}
