package org.dynamissky.vulkan.lut;

import org.vectrix.core.Vector3f;

/**
 * Lightweight camera state for sky per-frame compute passes.
 */
public record CameraState(
        Vector3f position,
        float nearPlane,
        float farPlane,
        Vector3f frustumTL,
        Vector3f frustumTR,
        Vector3f frustumBL) {

    public CameraState {
        if (position == null || frustumTL == null || frustumTR == null || frustumBL == null) {
            throw new IllegalArgumentException("camera vectors are required");
        }
        if (!Float.isFinite(nearPlane) || !Float.isFinite(farPlane) || nearPlane <= 0f || farPlane <= nearPlane) {
            throw new IllegalArgumentException("invalid near/far plane values");
        }
    }

    public static CameraState defaultState() {
        return new CameraState(
                new Vector3f(0f, 2f, 0f),
                0.1f,
                1000f,
                new Vector3f(-0.6f, 0.5f, -0.6f).normalize(),
                new Vector3f(0.6f, 0.5f, -0.6f).normalize(),
                new Vector3f(-0.6f, -0.5f, -0.6f).normalize());
    }
}
