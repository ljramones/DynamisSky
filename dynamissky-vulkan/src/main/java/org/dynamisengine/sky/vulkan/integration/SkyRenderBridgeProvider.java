package org.dynamisengine.sky.vulkan.integration;

import org.dynamisengine.light.impl.common.sky.SkyRenderBridge;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.vulkan.SkyConfig;
import org.dynamisengine.sky.vulkan.lut.GpuImage2DAlloc;
import org.dynamisengine.sky.vulkan.lut.GpuImage3DAlloc;
import org.dynamisengine.sky.vulkan.lut.GpuMemoryOps;
import org.dynamisengine.vectrix.core.Matrix4f;

/**
 * {@link SkyRenderBridge} implementation that delegates to {@link VulkanSkyIntegration}.
 */
public final class SkyRenderBridgeProvider implements SkyRenderBridge {

    private VulkanSkyIntegration integration;

    @Override
    public boolean initialize(InitContext context) {
        try {
            SkyConfig config = SkyConfig.builder().build();

            // Use the raw-handle create path. A stub GpuMemoryOps is provided;
            // the real GPU memory allocation is handled by the VulkanSkyService
            // once it receives the device handle.
            GpuMemoryOps memoryOps = new StubGpuMemoryOps();
            integration = VulkanSkyIntegration.create(
                    context.deviceHandle(), context.renderPass(),
                    memoryOps, context.descriptorPool(), config);
            return true;
        } catch (Throwable t) {
            integration = null;
            return false;
        }
    }

    @Override
    public void updateAndRecord(long commandBuffer, int frameIndex, float deltaTime,
                                float[] invViewProjMatrix, float[] viewProjMatrix) {
        if (integration == null) return;

        try {
            integration.updateDefaultCamera(commandBuffer, deltaTime, frameIndex);
            integration.recordBackground(commandBuffer, arrayToMatrix(invViewProjMatrix), frameIndex);
            integration.recordCelestial(commandBuffer, arrayToMatrix(viewProjMatrix), frameIndex);
        } catch (Throwable t) {
            // Sky rendering failed — disable to avoid repeated crashes
            integration = null;
        }
    }

    @Override
    public float[] sunDirection() {
        if (integration == null) return null;
        SunState sun = integration.getSunState();
        if (sun == null) return null;
        return new float[]{sun.direction().x(), sun.direction().y(), sun.direction().z()};
    }

    @Override
    public float[] sunColor() {
        if (integration == null) return null;
        SunState sun = integration.getSunState();
        if (sun == null) return null;
        return new float[]{sun.color().r(), sun.color().g(), sun.color().b()};
    }

    @Override
    public float sunIntensity() {
        if (integration == null) return 1.0f;
        SunState sun = integration.getSunState();
        if (sun == null) return 1.0f;
        return sun.intensity();
    }

    @Override
    public void shutdown() {
        if (integration != null) {
            integration.destroy();
            integration = null;
        }
    }

    private static Matrix4f arrayToMatrix(float[] a) {
        if (a == null || a.length < 16) {
            return new Matrix4f();
        }
        return new Matrix4f(
            a[0],  a[1],  a[2],  a[3],
            a[4],  a[5],  a[6],  a[7],
            a[8],  a[9],  a[10], a[11],
            a[12], a[13], a[14], a[15]
        );
    }

    /**
     * Stub GPU memory ops for SPI bootstrap. The sky service manages its own
     * memory lifecycle once initialized with the device handle.
     */
    private static final class StubGpuMemoryOps implements GpuMemoryOps {
        @Override
        public GpuImage2DAlloc createImage2D(int width, int height, int format) {
            throw new UnsupportedOperationException("SPI bridge does not allocate GPU images directly");
        }

        @Override
        public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
            throw new UnsupportedOperationException("SPI bridge does not allocate GPU images directly");
        }

        @Override
        public void destroyImage(long imageHandle, long memoryHandle) {
            // no-op
        }
    }
}
