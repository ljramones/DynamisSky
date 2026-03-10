package org.dynamissky.vulkan.internal.gpu;

import org.dynamissky.vulkan.SkyFrameContext;

/**
 * Internal anti-corruption seam for sky GPU/backend interactions.
 *
 * This is intentionally internal and non-SPI in A2.
 */
public interface SkyGpuBackendAdapter {
    void prepareFrame(SkyFrameContext frameContext);

    void transitionToShaderRead(long imageHandle);
}
