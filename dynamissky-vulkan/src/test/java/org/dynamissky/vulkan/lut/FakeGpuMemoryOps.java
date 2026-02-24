package org.dynamissky.vulkan.lut;

import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;

import java.util.concurrent.atomic.AtomicLong;

final class FakeGpuMemoryOps implements GpuMemoryOps {
    private final AtomicLong ids = new AtomicLong(1);

    @Override
    public GpuImage2DAlloc createImage2D(int width, int height, int format) {
        long image = ids.getAndIncrement();
        long memory = ids.getAndIncrement();
        return new GpuImage2DAlloc(new GpuImage2D(image, width, height), memory, format);
    }

    @Override
    public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
        long image = ids.getAndIncrement();
        long memory = ids.getAndIncrement();
        return new GpuImage3DAlloc(new GpuImage3D(image, width, height, depth), memory, format);
    }

    @Override
    public void destroyImage(long imageHandle, long memoryHandle) {
        // no-op
    }
}
