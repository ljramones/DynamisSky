package org.dynamissky.core;

import org.dynamissky.api.ColorRgb;
import org.dynamissky.api.Vec3;
import org.vectrix.core.Vector3f;

/**
 * Boundary adapters between API transfer types and Vectrix math types.
 */
public final class SkyTypeAdapters {
    private SkyTypeAdapters() {
    }

    public static Vec3 toApiVec3(Vector3f source) {
        return new Vec3(source.x(), source.y(), source.z());
    }

    public static Vector3f toVectrix(Vec3 source) {
        return new Vector3f(source.x(), source.y(), source.z());
    }

    public static ColorRgb toApiColor(Vector3f source) {
        return new ColorRgb(source.x(), source.y(), source.z());
    }

    public static Vector3f toVectrix(ColorRgb source) {
        return new Vector3f(source.r(), source.g(), source.b());
    }
}
