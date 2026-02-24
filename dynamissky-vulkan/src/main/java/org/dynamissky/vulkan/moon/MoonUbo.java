package org.dynamissky.vulkan.moon;

import org.dynamissky.api.state.MoonState;
import org.dynamissky.api.state.SunState;
import org.vectrix.core.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Per-frame moon billboard UBO payload.
 */
public record MoonUbo(ByteBuffer bytes) {
    public static final int SIZE_BYTES = 112;

    public static MoonUbo of(MoonState moonState, SunState sunState, Matrix4f viewProj) {
        return new MoonUbo(pack(moonState, sunState, viewProj));
    }

    public static ByteBuffer pack(MoonState moonState, SunState sunState, Matrix4f viewProj) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        buffer.putFloat(viewProj.m00()).putFloat(viewProj.m01()).putFloat(viewProj.m02()).putFloat(viewProj.m03());
        buffer.putFloat(viewProj.m10()).putFloat(viewProj.m11()).putFloat(viewProj.m12()).putFloat(viewProj.m13());
        buffer.putFloat(viewProj.m20()).putFloat(viewProj.m21()).putFloat(viewProj.m22()).putFloat(viewProj.m23());
        buffer.putFloat(viewProj.m30()).putFloat(viewProj.m31()).putFloat(viewProj.m32()).putFloat(viewProj.m33());

        buffer.putFloat(moonState.direction().x());
        buffer.putFloat(moonState.direction().y());
        buffer.putFloat(moonState.direction().z());
        buffer.putFloat(moonState.intensity());

        buffer.putFloat(sunState.direction().x());
        buffer.putFloat(sunState.direction().y());
        buffer.putFloat(sunState.direction().z());
        buffer.putFloat((float) (moonState.phase() * Math.PI));

        buffer.putFloat(moonState.angularRadiusDegrees() * 2f);
        buffer.putFloat(0f).putFloat(0f).putFloat(0f);

        buffer.flip();
        return buffer;
    }
}
