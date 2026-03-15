package org.dynamisengine.sky.api.gpu;

/**
 * Typed sky GPU buffer reference.
 */
public record SkyGpuBufferRef(long handle) {
    public static final SkyGpuBufferRef NULL = new SkyGpuBufferRef(0L);
}
