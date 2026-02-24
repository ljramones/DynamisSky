package org.dynamissky.core.solar;

/**
 * Geographic coordinates in degrees.
 */
public record LatLon(double latitudeDegrees, double longitudeDegrees) {
    public LatLon {
        if (!Double.isFinite(latitudeDegrees) || latitudeDegrees < -90d || latitudeDegrees > 90d) {
            throw new IllegalArgumentException("latitudeDegrees must be in [-90,90]");
        }
        if (!Double.isFinite(longitudeDegrees) || longitudeDegrees < -180d || longitudeDegrees > 180d) {
            throw new IllegalArgumentException("longitudeDegrees must be in [-180,180]");
        }
    }
}
