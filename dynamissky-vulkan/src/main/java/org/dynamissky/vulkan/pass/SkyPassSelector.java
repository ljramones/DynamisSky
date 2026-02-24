package org.dynamissky.vulkan.pass;

import org.dynamissky.api.descriptor.SkyModelType;

/**
 * Routes sky background recording to analytical LUT pass or HDRI fallback.
 */
public final class SkyPassSelector {
    private final SkyBackgroundPass skyBackgroundPass;
    private final HdriSkyPass hdriSkyPass;

    public SkyPassSelector(SkyBackgroundPass skyBackgroundPass, HdriSkyPass hdriSkyPass) {
        this.skyBackgroundPass = skyBackgroundPass;
        this.hdriSkyPass = hdriSkyPass;
    }

    public void record(long commandBuffer,
                       SkyPassUbo ubo,
                       SkyModelType activeModel,
                       float hdriRotation,
                       float hdriIntensity,
                       int frameIndex) {
        if (activeModel == SkyModelType.HDRI) {
            hdriSkyPass.record(commandBuffer, ubo, hdriRotation, hdriIntensity, frameIndex);
            return;
        }
        skyBackgroundPass.record(commandBuffer, ubo, activeModel, frameIndex);
    }

    public int backgroundRecordCount() {
        return skyBackgroundPass.recordCount();
    }

    public int hdriRecordCount() {
        return hdriSkyPass.recordCount();
    }
}
