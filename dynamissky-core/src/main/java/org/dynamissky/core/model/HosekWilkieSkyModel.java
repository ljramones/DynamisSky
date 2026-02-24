package org.dynamissky.core.model;

import org.dynamissky.core.model.hosek.HosekWilkieData;
import org.vectrix.core.Vector3f;

/**
 * Hosek-Wilkie-inspired approximation with embedded coefficients.
 */
public final class HosekWilkieSkyModel implements AnalyticalSkyModel {
    private final float turbidity;

    public HosekWilkieSkyModel(float turbidity) {
        if (!Float.isFinite(turbidity) || turbidity < 1f) {
            throw new IllegalArgumentException("turbidity must be finite and >= 1");
        }
        this.turbidity = turbidity;
    }

    @Override
    public Vector3f evaluate(Vector3f viewDir, Vector3f sunDir) {
        Vector3f v = new Vector3f(viewDir).normalize();
        Vector3f s = new Vector3f(sunDir).normalize();

        float cosTheta = java.lang.Math.max(0f, v.y);
        float cosGamma = java.lang.Math.max(-1f, java.lang.Math.min(1f, v.dot(s)));
        float gamma = (float) java.lang.Math.acos(cosGamma);

        float t = (turbidity - 1f) / 9f;
        float x = clamp(0.23f + 0.08f * (1f - cosTheta) + 0.03f * t + 0.01f * HosekWilkieData.RGB_COEFFICIENTS[0], 0.2f, 0.42f);
        float y = clamp(0.24f + 0.10f * cosTheta + 0.02f * t + 0.01f * HosekWilkieData.RGB_COEFFICIENTS[4], 0.2f, 0.45f);
        float Y = java.lang.Math.max(0.01f,
                (0.9f + 0.3f * cosTheta - 0.2f * gamma)
                        * (1f + 0.2f * t + 0.02f * HosekWilkieData.LUMINANCE_COEFFICIENTS[0]));

        return new Vector3f(x, y, Y);
    }

    private static float clamp(float value, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }
}
