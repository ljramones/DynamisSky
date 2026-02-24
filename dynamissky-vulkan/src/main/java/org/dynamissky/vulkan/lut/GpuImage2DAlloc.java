package org.dynamissky.vulkan.lut;

import org.dynamissky.api.gpu.GpuImage2D;

/**
 * 2D image allocation payload with backing memory handle.
 */
public record GpuImage2DAlloc(GpuImage2D image, long memoryHandle, int format) {
    public GpuImage2DAlloc {
        if (image == null) {
            throw new IllegalArgumentException("image is required");
        }
    }
}
