package org.dynamissky.core.solar;

/**
 * Time zone offset from UTC in hours.
 */
public record TimeZoneOffset(float hours) {
    public TimeZoneOffset {
        if (!Float.isFinite(hours) || hours < -14f || hours > 14f) {
            throw new IllegalArgumentException("hours must be in [-14,14]");
        }
    }
}
