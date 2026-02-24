package org.dynamissky.vulkan.lut;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Test-facing readback registry for scaffold bake parity checks.
 */
public final class SkyLutReadbackRegistry {
    private static final Map<Long, float[]> CENTER_PIXEL = new ConcurrentHashMap<>();

    private SkyLutReadbackRegistry() {
    }

    public static void writeCenterPixel(long imageHandle, float r, float g, float b, float a) {
        CENTER_PIXEL.put(imageHandle, new float[]{r, g, b, a});
    }

    public static float[] readCenterPixel(long imageHandle) {
        return CENTER_PIXEL.getOrDefault(imageHandle, new float[]{0f, 0f, 0f, 0f});
    }

    public static void clear(long imageHandle) {
        CENTER_PIXEL.remove(imageHandle);
    }
}
