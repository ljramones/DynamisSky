package org.dynamissky.vulkan.descriptor;

import org.dynamissky.vulkan.lut.SkyLutResources;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Owns descriptor set handles used by sky compute/draw pipelines.
 */
public final class SkyDescriptorSets {
    private static final AtomicLong IDS = new AtomicLong(1000);

    private final long atmosphereSet;
    private final long lutSamplerSet;
    private final long storageImageSet;
    private final long frameUniformSet;

    private SkyDescriptorSets(long atmosphereSet, long lutSamplerSet, long storageImageSet, long frameUniformSet) {
        this.atmosphereSet = atmosphereSet;
        this.lutSamplerSet = lutSamplerSet;
        this.storageImageSet = storageImageSet;
        this.frameUniformSet = frameUniformSet;
    }

    public static SkyDescriptorSets create(SkyLutResources luts) {
        return new SkyDescriptorSets(
                IDS.incrementAndGet(),
                IDS.incrementAndGet(),
                IDS.incrementAndGet(),
                IDS.incrementAndGet());
    }

    public long atmosphereSet() {
        return atmosphereSet;
    }

    public long lutSamplerSet() {
        return lutSamplerSet;
    }

    public long storageImageSet() {
        return storageImageSet;
    }

    public long frameUniformSet() {
        return frameUniformSet;
    }

    public void destroy() {
        // Descriptor pool teardown is integrated in renderer step.
    }
}
