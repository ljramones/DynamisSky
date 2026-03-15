package org.dynamisengine.sky.api.service;

import org.dynamisengine.sky.api.config.AtmosphereConfig;
import org.dynamisengine.sky.api.descriptor.SkyDescriptor;
import org.dynamisengine.sky.api.state.MoonState;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.api.state.TimeOfDayState;
import org.dynamisengine.sky.api.state.WeatherState;
import org.dynamisengine.sky.api.gpu.SkyGpuResources;

/**
 * Main API for reading and mutating sky system state.
 */
public interface SkyService {
    SunState sunState();

    MoonState moonState();

    TimeOfDayState timeOfDayState();

    WeatherState weatherState();

    AtmosphereConfig atmosphereConfig();

    SkyDescriptor skyDescriptor();

    SkyGpuResources gpuResources();

    void applySkyDescriptor(SkyDescriptor descriptor);

    void setWeatherState(WeatherState weatherState);

    void setTimeMultiplier(double timeMultiplier);

    void setTimeOfDayHours(double localTimeHours);

    void lockTimeOfDay();

    void unlockTimeOfDay();
}
