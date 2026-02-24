package org.dynamissky.vulkan.lut;

import org.dynamissky.api.state.SunState;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * std140-packed per-frame UBO for aerial perspective LUT pass.
 */
public final class AerialUbo {
    public static final int SIZE_BYTES = 96;

    private AerialUbo() {
    }

    public static ByteBuffer pack(SunState sunState, CameraState camera) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(SIZE_BYTES).order(ByteOrder.nativeOrder());

        buffer.putFloat(sunState.direction().x());
        buffer.putFloat(sunState.direction().y());
        buffer.putFloat(sunState.direction().z());
        buffer.putFloat(sunState.intensity());

        buffer.putFloat(camera.position().x());
        buffer.putFloat(camera.position().y());
        buffer.putFloat(camera.position().z());
        buffer.putFloat(camera.nearPlane());

        putVec4(buffer, camera.frustumTL().x(), camera.frustumTL().y(), camera.frustumTL().z(), 0f);
        putVec4(buffer, camera.frustumTR().x(), camera.frustumTR().y(), camera.frustumTR().z(), 0f);
        putVec4(buffer, camera.frustumBL().x(), camera.frustumBL().y(), camera.frustumBL().z(), 0f);

        buffer.putFloat(camera.farPlane());
        buffer.putFloat(0f);
        buffer.putFloat(0f);
        buffer.putFloat(0f);

        buffer.flip();
        return buffer;
    }

    private static void putVec4(ByteBuffer buffer, float x, float y, float z, float w) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);
        buffer.putFloat(w);
    }
}
