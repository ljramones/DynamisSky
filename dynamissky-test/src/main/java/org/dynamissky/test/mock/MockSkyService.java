package org.dynamissky.test.mock;

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

/**
 * Deterministic in-memory SkyService mock for unit and integration tests.
 */
public final class MockSkyService implements SkyService {
    public static final TimeOfDayState SOLAR_NOON =
            new TimeOfDayState(2451545.5, 12.0, 1.0, false, 1.0f, 5500);
    public static final TimeOfDayState CIVIL_TWILIGHT =
            new TimeOfDayState(2451545.25, 18.0, 1.0, false, 0.2f, 3200);
    public static final TimeOfDayState MIDNIGHT =
            new TimeOfDayState(2451545.0, 0.0, 1.0, false, 0.05f, 4100);

    public static final SunState NOON_TORONTO =
            new SunState(new Vec3(0.01f, 0.95f, -0.25f), new ColorRgb(1.0f, 0.95f, 0.9f), 1.0f, 180.0, 69.5);
    public static final SunState SUNSET =
            new SunState(new Vec3(-0.7f, 0.05f, -0.7f), new ColorRgb(1.0f, 0.55f, 0.35f), 0.25f, 270.0, 2.5);
    public static final SunState BELOW_HORIZON =
            new SunState(new Vec3(-0.2f, -0.4f, -0.9f), new ColorRgb(0.2f, 0.25f, 0.4f), 0.05f, 320.0, -18.0);

    public static final MoonState FULL_MOON =
            new MoonState(new Vec3(0.3f, 0.6f, -0.74f), new ColorRgb(0.6f, 0.62f, 0.65f), 0.2f, 1.0f, 0.53f);

    private TimeOfDayState timeOfDay = SOLAR_NOON;
    private SunState sunState = NOON_TORONTO;
    private MoonState moonState = FULL_MOON;
    private WeatherState weather = WeatherState.CLEAR;
    private AtmosphereConfig atmosphere = AtmosphereConfig.EARTH_STANDARD;
    private SkyDescriptor descriptor = SkyDescriptor.builder()
            .model(SkyModelType.HOSEK_WILKIE)
            .atmosphere(atmosphere)
            .weather(weather)
            .build();

    private int updateCallCount;
    private int setWeatherCallCount;
    private int setSkyDescriptorCallCount;

    public MockSkyService withTimeOfDay(TimeOfDayState state) {
        this.timeOfDay = state;
        return this;
    }

    public MockSkyService withSunState(SunState state) {
        this.sunState = state;
        return this;
    }

    public MockSkyService withMoonState(MoonState state) {
        this.moonState = state;
        return this;
    }

    public MockSkyService withWeather(WeatherState state) {
        this.weather = state;
        return this;
    }

    public void advance(float deltaSeconds) {
        updateCallCount++;
        double newHour = (timeOfDay.localTimeHours() + (deltaSeconds * timeOfDay.timeMultiplier()) / 3600.0) % 24.0;
        if (newHour < 0) {
            newHour += 24.0;
        }

        double newJd = timeOfDay.julianDate() + (deltaSeconds * timeOfDay.timeMultiplier()) / 86400.0;
        int kelvin = colorTempForHour(newHour);
        float ambient = ambientForHour(newHour);

        this.timeOfDay = new TimeOfDayState(newJd, newHour, timeOfDay.timeMultiplier(), false, ambient, kelvin);

        if (newHour >= 6.0 && newHour <= 18.0) {
            this.sunState = NOON_TORONTO;
        } else if (newHour > 18.0 && newHour < 20.0) {
            this.sunState = SUNSET;
        } else {
            this.sunState = BELOW_HORIZON;
        }
    }

    public int updateCallCount() {
        return updateCallCount;
    }

    public int setWeatherCallCount() {
        return setWeatherCallCount;
    }

    public int setSkyDescriptorCallCount() {
        return setSkyDescriptorCallCount;
    }

    public TimeOfDayState lastTimeOfDay() {
        return timeOfDay;
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
        return timeOfDay;
    }

    @Override
    public WeatherState weatherState() {
        return weather;
    }

    @Override
    public AtmosphereConfig atmosphereConfig() {
        return atmosphere;
    }

    @Override
    public SkyDescriptor skyDescriptor() {
        return descriptor;
    }

    @Override
    public SkyGpuResources gpuResources() {
        return SkyGpuResources.NULL;
    }

    @Override
    public void applySkyDescriptor(SkyDescriptor descriptor) {
        setSkyDescriptorCallCount++;
        this.descriptor = descriptor;
        this.atmosphere = descriptor.atmosphere();
    }

    @Override
    public void setWeatherState(WeatherState weatherState) {
        setWeatherCallCount++;
        this.weather = weatherState;
    }

    @Override
    public void setTimeMultiplier(double timeMultiplier) {
        this.timeOfDay = new TimeOfDayState(
                timeOfDay.julianDate(),
                timeOfDay.localTimeHours(),
                timeMultiplier,
                timeOfDay.locked(),
                timeOfDay.ambientIntensity(),
                timeOfDay.colorTemperatureKelvin());
    }

    @Override
    public void setTimeOfDayHours(double localTimeHours) {
        int kelvin = colorTempForHour(localTimeHours);
        float ambient = ambientForHour(localTimeHours);
        this.timeOfDay = new TimeOfDayState(
                timeOfDay.julianDate(),
                localTimeHours,
                timeOfDay.timeMultiplier(),
                timeOfDay.locked(),
                ambient,
                kelvin);
    }

    @Override
    public void lockTimeOfDay() {
        this.timeOfDay = new TimeOfDayState(
                timeOfDay.julianDate(),
                timeOfDay.localTimeHours(),
                timeOfDay.timeMultiplier(),
                true,
                timeOfDay.ambientIntensity(),
                timeOfDay.colorTemperatureKelvin());
    }

    @Override
    public void unlockTimeOfDay() {
        this.timeOfDay = new TimeOfDayState(
                timeOfDay.julianDate(),
                timeOfDay.localTimeHours(),
                timeOfDay.timeMultiplier(),
                false,
                timeOfDay.ambientIntensity(),
                timeOfDay.colorTemperatureKelvin());
    }

    private static int colorTempForHour(double hour) {
        if (hour < 6.0) return 4100;
        if (hour < 8.0) return 2200;
        if (hour < 14.0) return 5500;
        if (hour < 19.0) return 1800;
        return 4100;
    }

    private static float ambientForHour(double hour) {
        if (hour >= 6.0 && hour <= 18.0) return 1.0f;
        if (hour > 18.0 && hour < 20.0) return 0.2f;
        return 0.05f;
    }
}
