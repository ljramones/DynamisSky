package org.dynamissky.core.builder;

import org.dynamissky.api.Vec3;
import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.descriptor.SkyDescriptor;
import org.dynamissky.api.descriptor.SkyModelType;
import org.dynamissky.api.state.WeatherState;

/**
 * Core-side stricter descriptor builder with explicit required model.
 */
public final class SkyDescriptorBuilder {
    private SkyModelType model;
    private AtmosphereConfig atmosphere = AtmosphereConfig.EARTH_STANDARD;
    private float turbidity = 2.0f;
    private Vec3 groundAlbedo = new Vec3(0.1f, 0.1f, 0.1f);
    private WeatherState weather = WeatherState.CLEAR;
    private String hdriPath;
    private float hdriRotation;

    public static SkyDescriptorBuilder create() {
        return new SkyDescriptorBuilder();
    }

    public SkyDescriptorBuilder model(SkyModelType value) {
        this.model = value;
        return this;
    }

    public SkyDescriptorBuilder atmosphere(AtmosphereConfig value) {
        this.atmosphere = value;
        return this;
    }

    public SkyDescriptorBuilder turbidity(float value) {
        this.turbidity = value;
        return this;
    }

    public SkyDescriptorBuilder groundAlbedo(Vec3 value) {
        this.groundAlbedo = value;
        return this;
    }

    public SkyDescriptorBuilder weather(WeatherState value) {
        this.weather = value;
        return this;
    }

    public SkyDescriptorBuilder hdriPath(String value) {
        this.hdriPath = value;
        return this;
    }

    public SkyDescriptorBuilder hdriRotationDegrees(float value) {
        this.hdriRotation = value;
        return this;
    }

    public SkyDescriptor build() {
        if (model == null) {
            throw new IllegalStateException("model is required");
        }
        return new SkyDescriptor(model, atmosphere, turbidity, groundAlbedo, weather, hdriPath, hdriRotation);
    }
}
