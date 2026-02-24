package org.dynamissky.core.model;

import org.vectrix.core.Vector3f;

/**
 * Lightweight Preetham-style approximation with xyY output.
 */
public final class PreethamSkyModel implements AnalyticalSkyModel {
    private final float turbidity;

    public PreethamSkyModel(float turbidity) {
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

        float x = clamp(0.26f + 0.03f * (1f - cosTheta) + 0.008f * turbidity, 0.2f, 0.4f);
        float y = clamp(0.28f + 0.03f * cosTheta + 0.004f * turbidity, 0.2f, 0.45f);
        float Y = java.lang.Math.max(0.01f, (1.0f + cosTheta) * 0.5f * (1.2f - 0.25f * gamma));

        return new Vector3f(x, y, Y);
    }

    private static float clamp(float value, float min, float max) {
        return java.lang.Math.max(min, java.lang.Math.min(max, value));
    }
}
