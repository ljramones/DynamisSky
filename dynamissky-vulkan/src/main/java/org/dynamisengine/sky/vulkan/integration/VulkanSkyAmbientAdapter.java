package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.sky.api.state.TimeOfDayState;
import org.dynamisengine.vectrix.color.ColorSciencef;
import org.dynamisengine.vectrix.core.Vector3f;

/**
 * Converts time-of-day state to ambient lighting payload.
 */
public final class VulkanSkyAmbientAdapter {
    private VulkanSkyAmbientAdapter() {
    }

    public static AmbientLightData adapt(TimeOfDayState timeOfDay) {
        Vector3f rgb = ColorSciencef.kelvinToLinearRgb(timeOfDay.colorTemperatureKelvin(), new Vector3f());
        return new AmbientLightData(rgb, timeOfDay.ambientIntensity());
    }
}
