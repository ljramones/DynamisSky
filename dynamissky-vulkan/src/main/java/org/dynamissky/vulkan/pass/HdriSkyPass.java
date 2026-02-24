package org.dynamissky.vulkan.pass;

import org.dynamissky.vulkan.descriptor.SkyDescriptorSets;
import org.dynamissky.vulkan.lut.GpuMemoryOps;

import java.util.concurrent.atomic.AtomicLong;

/**
 * HDRI fallback full-screen pass.
 */
public final class HdriSkyPass {
    private static final AtomicLong IDS = new AtomicLong(20_000);

    private final long pipelineHandle;
    private boolean hdriLoaded;
    private int hdriWidth;
    private int hdriHeight;
    private int recordCount;

    private HdriSkyPass(long pipelineHandle) {
        this.pipelineHandle = pipelineHandle;
    }

    public static HdriSkyPass create(long device, long renderPass, GpuMemoryOps memoryOps, SkyDescriptorSets descriptorSets) {
        return new HdriSkyPass(IDS.incrementAndGet());
    }

    public void loadHdri(String path, long commandBuffer) {
        // File decode wrapper is deferred to asset pipeline integration.
        loadHdri(new float[]{1f, 1f, 1f, 1f}, 1, 1, commandBuffer);
    }

    public void loadHdri(float[] rgbaPixels, int width, int height, long commandBuffer) {
        if (rgbaPixels == null || rgbaPixels.length == 0 || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("invalid HDRI payload");
        }
        this.hdriLoaded = true;
        this.hdriWidth = width;
        this.hdriHeight = height;
    }

    public void record(long commandBuffer, SkyPassUbo ubo, float rotation, float intensity, int frameIndex) {
        if (!hdriLoaded) {
            // Safe fallback for tests and early frames.
            loadHdri(new float[]{0.5f, 0.6f, 0.8f, 1f}, 1, 1, commandBuffer);
        }
        recordCount++;
    }

    public long pipelineHandle() {
        return pipelineHandle;
    }

    public int recordCount() {
        return recordCount;
    }

    public boolean hdriLoaded() {
        return hdriLoaded;
    }

    public int hdriWidth() {
        return hdriWidth;
    }

    public int hdriHeight() {
        return hdriHeight;
    }

    public void destroy() {
        // Vulkan pipeline/image teardown in renderer integration step.
    }
}
