package org.dynamissky.core.solar;

import org.vectrix.core.Vector3f;

/**
 * Solar azimuth/altitude in degrees.
 */
public record SolarPosition(double azimuthDegrees, double altitudeDegrees) {

    public Vector3f toWorldDirection(Vector3f northAxis) {
        if (northAxis == null || northAxis.lengthSquared() == 0f) {
            throw new IllegalArgumentException("northAxis must be non-zero");
        }

        float altRad = (float) java.lang.Math.toRadians(altitudeDegrees);
        float aziRad = (float) java.lang.Math.toRadians(azimuthDegrees);

        Vector3f up = new Vector3f(0f, 1f, 0f);
        Vector3f north = new Vector3f(northAxis.x, 0f, northAxis.z);
        if (north.lengthSquared() < 1e-6f) {
            north.set(0f, 0f, -1f);
        }
        north.normalize();

        Vector3f east = new Vector3f(north).cross(up).normalize();

        float horizontal = (float) java.lang.Math.cos(altRad);

        return new Vector3f(north).mul(horizontal * (float) java.lang.Math.cos(aziRad))
                .add(new Vector3f(east).mul(horizontal * (float) java.lang.Math.sin(aziRad)))
                .add(new Vector3f(up).mul((float) java.lang.Math.sin(altRad)))
                .normalize();
    }
}
