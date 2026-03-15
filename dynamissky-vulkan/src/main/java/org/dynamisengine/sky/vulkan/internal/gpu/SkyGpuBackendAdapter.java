package org.dynamisengine.sky.vulkan.internal.gpu;

import org.dynamisengine.sky.vulkan.SkyFrameContext;

/**
 * Internal anti-corruption seam for sky GPU/backend interactions.
 *
 * This is intentionally internal and non-SPI in A2.
 */
public interface SkyGpuBackendAdapter {
    void prepareFrame(SkyFrameContext frameContext);

    void transitionToShaderRead(long imageHandle);
}
