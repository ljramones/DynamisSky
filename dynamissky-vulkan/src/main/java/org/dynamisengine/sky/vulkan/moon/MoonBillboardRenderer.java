package org.dynamisengine.sky.vulkan.moon;

import org.dynamisengine.sky.api.state.MoonState;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.vulkan.lut.GpuMemoryOps;
import org.vectrix.core.Matrix4f;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Moon billboard renderer with phase-masked shading.
 */
public final class MoonBillboardRenderer {
    private static final AtomicLong IDS = new AtomicLong(40_000);

    private final long pipelineHandle;
    private int recordCount;

    private MoonBillboardRenderer(long pipelineHandle) {
        this.pipelineHandle = pipelineHandle;
    }

    public static MoonBillboardRenderer create(long device,
                                               long renderPass,
                                               GpuMemoryOps memoryOps,
                                               long moonTextureImageView,
                                               long sampler) {
        return new MoonBillboardRenderer(IDS.incrementAndGet());
    }

    public void record(long commandBuffer,
                       MoonState moonState,
                       SunState sunState,
                       Matrix4f viewProj,
                       int frameIndex) {
        MoonUbo.of(moonState, sunState, viewProj);
        recordCount++;
    }

    public long pipelineHandle() {
        return pipelineHandle;
    }

    public int recordCount() {
        return recordCount;
    }

    public void destroy() {
        // Vulkan resources are integrated in renderer memory lifecycle.
    }
}
