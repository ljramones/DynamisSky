package org.dynamissky.vulkan.internal.gpu;

import org.dynamissky.vulkan.SkyFrameContext;

/**
 * Internal default adapter stub used during seam introduction.
 */
public final class NoopSkyGpuBackendAdapter implements SkyGpuBackendAdapter {
    @Override
    public void prepareFrame(final SkyFrameContext frameContext) {
        // A2 stub: no runtime behavior changes.
    }

    @Override
    public void transitionToShaderRead(final long imageHandle) {
        // A2 stub: no runtime behavior changes.
    }
}
