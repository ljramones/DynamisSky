package org.dynamissky.core.solar;

/**
 * NOAA-style solar position calculator.
 */
public final class SolarPositionCalculator {
    private SolarPositionCalculator() {
    }

    public static SolarPosition compute(JulianDate julianDate, LatLon latLon, TimeZoneOffset ignored) {
        return compute(julianDate.value(), latLon);
    }

    public static SolarPosition compute(double julianDateUtc, LatLon latLon) {
        double t = (julianDateUtc - 2451545.0) / 36525.0;

        double l0 = normalizeDegrees(280.46646 + t * (36000.76983 + 0.0003032 * t));
        double m = normalizeDegrees(357.52911 + t * (35999.05029 - 0.0001537 * t));
        double e = 0.016708634 - t * (0.000042037 + 0.0000001267 * t);

        double c = java.lang.Math.sin(toRadians(m)) * (1.914602 - t * (0.004817 + 0.000014 * t))
                + java.lang.Math.sin(toRadians(2 * m)) * (0.019993 - 0.000101 * t)
                + java.lang.Math.sin(toRadians(3 * m)) * 0.000289;

        double sunTrueLongitude = l0 + c;
        double omega = 125.04 - 1934.136 * t;
        double sunApparentLongitude = sunTrueLongitude - 0.00569 - 0.00478 * java.lang.Math.sin(toRadians(omega));

        double meanObliquity = 23.0 + (26.0 + ((21.448 - t * (46.815 + t * (0.00059 - t * 0.001813))) / 60.0)) / 60.0;
        double obliquityCorrection = meanObliquity + 0.00256 * java.lang.Math.cos(toRadians(omega));

        double declination = toDegrees(java.lang.Math.asin(
                java.lang.Math.sin(toRadians(obliquityCorrection)) * java.lang.Math.sin(toRadians(sunApparentLongitude))));

        double y = java.lang.Math.tan(toRadians(obliquityCorrection / 2.0));
        y *= y;

        double eqTime = 4.0 * toDegrees(
                y * java.lang.Math.sin(2.0 * toRadians(l0))
                        - 2.0 * e * java.lang.Math.sin(toRadians(m))
                        + 4.0 * e * y * java.lang.Math.sin(toRadians(m)) * java.lang.Math.cos(2.0 * toRadians(l0))
                        - 0.5 * y * y * java.lang.Math.sin(4.0 * toRadians(l0))
                        - 1.25 * e * e * java.lang.Math.sin(2.0 * toRadians(m))
        );

        double fractionalDayUtc = (julianDateUtc + 0.5) - java.lang.Math.floor(julianDateUtc + 0.5);
        double utcMinutes = fractionalDayUtc * 1440.0;
        double trueSolarMinutes = normalizeMinutes(utcMinutes + eqTime + 4.0 * latLon.longitudeDegrees());

        double hourAngle = trueSolarMinutes / 4.0 - 180.0;

        double latRad = toRadians(latLon.latitudeDegrees());
        double decRad = toRadians(declination);
        double haRad = toRadians(hourAngle);

        double cosZenith = java.lang.Math.sin(latRad) * java.lang.Math.sin(decRad)
                + java.lang.Math.cos(latRad) * java.lang.Math.cos(decRad) * java.lang.Math.cos(haRad);
        cosZenith = java.lang.Math.max(-1.0, java.lang.Math.min(1.0, cosZenith));

        double zenith = toDegrees(java.lang.Math.acos(cosZenith));
        double altitude = 90.0 - zenith;

        double azDen = java.lang.Math.cos(latRad) * java.lang.Math.sin(toRadians(zenith));
        double azimuth;
        if (java.lang.Math.abs(azDen) < 1e-9) {
            azimuth = 180.0;
        } else {
            double az = (java.lang.Math.sin(latRad) * java.lang.Math.cos(toRadians(zenith)) - java.lang.Math.sin(decRad)) / azDen;
            az = java.lang.Math.max(-1.0, java.lang.Math.min(1.0, az));
            azimuth = toDegrees(java.lang.Math.acos(az));
            if (hourAngle > 0) {
                azimuth = 360.0 - azimuth;
            }
        }

        return new SolarPosition(normalizeDegrees(azimuth), altitude);
    }

    private static double toRadians(double degrees) {
        return java.lang.Math.toRadians(degrees);
    }

    private static double toDegrees(double radians) {
        return java.lang.Math.toDegrees(radians);
    }

    private static double normalizeDegrees(double degrees) {
        double result = degrees % 360.0;
        return result < 0 ? result + 360.0 : result;
    }

    private static double normalizeMinutes(double minutes) {
        double result = minutes % 1440.0;
        return result < 0 ? result + 1440.0 : result;
    }
}
