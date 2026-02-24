package org.dynamissky.vulkan.stars;

import java.nio.ByteBuffer;

/**
 * Packed star vertex for GPU upload.
 */
public record StarFieldVertex(
        float x, float y, float z,
        float magnitude,
        float r, float g, float b,
        float pad) {

    public static final int STRIDE = 32;

    public ByteBuffer pack(ByteBuffer buffer) {
        buffer.putFloat(x).putFloat(y).putFloat(z);
        buffer.putFloat(magnitude);
        buffer.putFloat(r).putFloat(g).putFloat(b);
        buffer.putFloat(pad);
        return buffer;
    }
}
