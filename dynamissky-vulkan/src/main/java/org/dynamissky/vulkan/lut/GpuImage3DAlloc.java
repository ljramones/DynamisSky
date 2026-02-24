package org.dynamissky.vulkan.lut;

import org.dynamissky.api.gpu.GpuImage3D;

/**
 * 3D image allocation payload with backing memory handle.
 */
public record GpuImage3DAlloc(GpuImage3D image, long memoryHandle, int format) {
    public GpuImage3DAlloc {
        if (image == null) {
            throw new IllegalArgumentException("image is required");
        }
    }
}
