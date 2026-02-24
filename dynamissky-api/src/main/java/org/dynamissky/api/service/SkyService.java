package org.dynamissky.api.service;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.descriptor.SkyDescriptor;
import org.dynamissky.api.state.MoonState;
import org.dynamissky.api.state.SunState;
import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.api.state.WeatherState;
import org.dynamissky.api.gpu.SkyGpuResources;

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
