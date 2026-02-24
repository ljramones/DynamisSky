package org.dynamissky.vulkan.integration;

import org.dynamissky.api.state.SunState;
import org.vectrix.core.Matrix4f;
import org.vectrix.core.Vector3f;

/**
 * Converts SunState to DLE directional light data.
 */
public final class VulkanSkySunLightAdapter {
    private VulkanSkySunLightAdapter() {
    }

    public static DirectionalLightData adapt(SunState sunState) {
        Vector3f dir = new Vector3f(
                sunState.direction().x(),
                sunState.direction().y(),
                sunState.direction().z()).normalize();

        Vector3f color = new Vector3f(
                sunState.color().r(),
                sunState.color().g(),
                sunState.color().b());

        Matrix4f shadow = buildSunShadowMatrix(sunState, 200f, 200f);
        return new DirectionalLightData(dir, color, sunState.intensity(), shadow);
    }

    public static Matrix4f buildSunShadowMatrix(SunState sunState,
                                                float shadowDistance,
                                                float shadowDepth) {
        Vector3f direction = new Vector3f(
                sunState.direction().x(),
                sunState.direction().y(),
                sunState.direction().z()).normalize();

        Vector3f eye = new Vector3f(direction).mul(-shadowDistance);
        Vector3f center = new Vector3f(0f, 0f, 0f);
        Vector3f up = java.lang.Math.abs(direction.y()) > 0.99f
                ? new Vector3f(0f, 0f, 1f)
                : new Vector3f(0f, 1f, 0f);

        Matrix4f view = new Matrix4f().identity().setLookAt(eye, center, up);
        Matrix4f proj = new Matrix4f().identity().setOrtho(
                -shadowDistance,
                shadowDistance,
                -shadowDistance,
                shadowDistance,
                -shadowDepth,
                shadowDepth);

        return proj.mul(view, new Matrix4f());
    }
}
