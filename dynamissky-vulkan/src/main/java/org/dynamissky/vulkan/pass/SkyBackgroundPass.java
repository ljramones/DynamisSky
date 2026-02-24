package org.dynamissky.vulkan.pass;

import org.dynamissky.api.descriptor.SkyModelType;
import org.dynamissky.vulkan.descriptor.SkyDescriptorSets;
import org.dynamissky.vulkan.lut.SkyLutResources;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Full-screen sky background draw pass sampling sky view LUT.
 */
public final class SkyBackgroundPass {
    private static final AtomicLong IDS = new AtomicLong(10_000);

    private final long pipelineHandle;
    private int recordCount;

    private SkyBackgroundPass(long pipelineHandle) {
        this.pipelineHandle = pipelineHandle;
    }

    public static SkyBackgroundPass create(long device, long renderPass, SkyLutResources luts, SkyDescriptorSets descriptorSets) {
        return new SkyBackgroundPass(IDS.incrementAndGet());
    }

    public void record(long commandBuffer, SkyPassUbo ubo, SkyModelType activeModel, int frameIndex) {
        if (ubo == null) {
            throw new IllegalArgumentException("ubo is required");
        }
        recordCount++;
    }

    public long pipelineHandle() {
        return pipelineHandle;
    }

    public int recordCount() {
        return recordCount;
    }

    public void destroy() {
        // Vulkan pipeline teardown in renderer integration step.
    }
}
