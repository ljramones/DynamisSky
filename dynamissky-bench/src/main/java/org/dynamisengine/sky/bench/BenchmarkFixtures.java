package org.dynamisengine.sky.bench;

import org.dynamisengine.sky.api.config.AtmosphereConfig;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.api.ColorRgb;
import org.dynamisengine.sky.api.Vec3;
import org.dynamisengine.vectrix.core.Vector3f;

/**
 * Shared immutable benchmark fixtures.
 */
public final class BenchmarkFixtures {
    public static final AtmosphereConfig EARTH = AtmosphereConfig.EARTH_STANDARD;
    public static final SunState NOON_SUN = new SunState(
            new Vec3(0f, 0.9f, 0f),
            new ColorRgb(1f, 0.98f, 0.9f),
            1f,
            180.0,
            60.0);
    public static final Vector3f ZENITH = new Vector3f(0f, 1f, 0f);
    public static final Vector3f HORIZON = new Vector3f(1f, 0f, 0f);

    public static final float TURBIDITY_CLEAR = 2.0f;
    public static final float TURBIDITY_HAZY = 7.0f;

    private BenchmarkFixtures() {
    }
}
