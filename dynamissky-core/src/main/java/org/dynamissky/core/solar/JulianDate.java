package org.dynamissky.core.solar;

/**
 * Julian date value in UTC.
 */
public record JulianDate(double value) {
    public JulianDate {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("value must be finite");
        }
    }

    public static JulianDate of(int year, int month, int day, int hour, int minute, int second) {
        int y = month <= 2 ? year - 1 : year;
        int m = month <= 2 ? month + 12 : month;

        int a = y / 100;
        int b = 2 - a + a / 4;

        double dayFraction = (hour + (minute / 60.0) + (second / 3600.0)) / 24.0;
        double jd = java.lang.Math.floor(365.25 * (y + 4716))
                + java.lang.Math.floor(30.6001 * (m + 1))
                + day + dayFraction + b - 1524.5;

        return new JulianDate(jd);
    }
}
