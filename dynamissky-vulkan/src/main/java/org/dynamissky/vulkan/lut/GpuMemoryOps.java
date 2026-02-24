package org.dynamissky.vulkan.lut;

/**
 * Minimal GPU memory abstraction for Sky LUT allocation.
 */
public interface GpuMemoryOps {
    GpuImage2DAlloc createImage2D(int width, int height, int format);

    GpuImage3DAlloc createImage3D(int width, int height, int depth, int format);

    void destroyImage(long imageHandle, long memoryHandle);
}
