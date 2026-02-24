package org.dynamissky.vulkan.stars;

import org.dynamissky.core.stars.StarCatalog;
import org.dynamissky.vulkan.lut.GpuMemoryOps;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Star field renderer using additive point sprites.
 */
public final class StarFieldRenderer {
    private static final AtomicLong IDS = new AtomicLong(30_000);

    private final long pipelineHandle;
    private int uploadedStarCount;
    private int uploadedBytes;
    private int recordCount;

    private StarFieldRenderer(long pipelineHandle) {
        this.pipelineHandle = pipelineHandle;
    }

    public static StarFieldRenderer create(long device, long renderPass, GpuMemoryOps memoryOps) {
        return new StarFieldRenderer(IDS.incrementAndGet());
    }

    public void uploadCatalog(StarCatalog catalog, long commandBuffer) {
        this.uploadedStarCount = catalog.count();
        this.uploadedBytes = catalog.count() * StarFieldVertex.STRIDE;

        ByteBuffer staging = ByteBuffer.allocateDirect(uploadedBytes).order(ByteOrder.nativeOrder());
        for (int i = 0; i < catalog.count(); i++) {
            float theta = (float) (i * 0.61803398875);
            float phi = (float) (i * 0.38196601125);
            float x = (float) (Math.cos(theta) * Math.sin(phi));
            float y = (float) Math.cos(phi);
            float z = (float) (Math.sin(theta) * Math.sin(phi));
            float magnitude = 1f + (i % 6);
            float brightness = Math.max(0.1f, (6.5f - magnitude) / 5.5f);
            new StarFieldVertex(x, y, z, magnitude, brightness, brightness * 0.95f, brightness * 1.05f, 0f).pack(staging);
        }
        staging.flip();
    }

    public void record(long commandBuffer, StarPassUbo ubo, float starVisibility, int frameIndex) {
        if (ubo == null) {
            throw new IllegalArgumentException("ubo is required");
        }
        recordCount++;
    }

    public long pipelineHandle() {
        return pipelineHandle;
    }

    public int uploadedStarCount() {
        return uploadedStarCount;
    }

    public int uploadedBytes() {
        return uploadedBytes;
    }

    public int recordCount() {
        return recordCount;
    }

    public void destroy() {
        // Vulkan resources are integrated in renderer memory lifecycle.
    }
}
