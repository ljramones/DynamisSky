package org.dynamissky.vulkan.pass;

import org.vectrix.core.Matrix4f;
import org.vectrix.core.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Per-frame sky background draw uniform payload.
 */
public record SkyPassUbo(ByteBuffer bytes) {
    public static final int SIZE_BYTES = 80;

    public static SkyPassUbo of(Matrix4f invViewProj, Vector3f sunDirection) {
        return new SkyPassUbo(pack(invViewProj, sunDirection));
    }

    public static ByteBuffer pack(Matrix4f invViewProj, Vector3f sunDirection) {
        if (invViewProj == null || sunDirection == null) {
            throw new IllegalArgumentException("invViewProj and sunDirection are required");
        }

        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        buffer.putFloat(invViewProj.m00()).putFloat(invViewProj.m01()).putFloat(invViewProj.m02()).putFloat(invViewProj.m03());
        buffer.putFloat(invViewProj.m10()).putFloat(invViewProj.m11()).putFloat(invViewProj.m12()).putFloat(invViewProj.m13());
        buffer.putFloat(invViewProj.m20()).putFloat(invViewProj.m21()).putFloat(invViewProj.m22()).putFloat(invViewProj.m23());
        buffer.putFloat(invViewProj.m30()).putFloat(invViewProj.m31()).putFloat(invViewProj.m32()).putFloat(invViewProj.m33());

        buffer.putFloat(sunDirection.x());
        buffer.putFloat(sunDirection.y());
        buffer.putFloat(sunDirection.z());
        buffer.putFloat(0f);

        buffer.flip();
        return buffer;
    }
}
