package org.dynamissky.vulkan.integration;

import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.core.color.KelvinToRgb;
import org.vectrix.core.Vector3f;

/**
 * Converts time-of-day state to ambient lighting payload.
 */
public final class VulkanSkyAmbientAdapter {
    private VulkanSkyAmbientAdapter() {
    }

    public static AmbientLightData adapt(TimeOfDayState timeOfDay) {
        Vector3f rgb = KelvinToRgb.toLinearRgb(timeOfDay.colorTemperatureKelvin(), new Vector3f());
        return new AmbientLightData(rgb, timeOfDay.ambientIntensity());
    }
}
