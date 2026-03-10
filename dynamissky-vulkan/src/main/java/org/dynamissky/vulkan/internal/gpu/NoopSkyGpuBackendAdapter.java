package org.dynamissky.vulkan.internal.gpu;

import org.dynamissky.vulkan.SkyFrameContext;

/**
 * Internal default adapter stub used during A1 seam introduction.
 */
public final class NoopSkyGpuBackendAdapter implements SkyGpuBackendAdapter {
    @Override
    public void prepareFrame(final SkyFrameContext frameContext) {
        // A1 stub: no runtime behavior changes.
    }
}
