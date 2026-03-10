package org.dynamissky.vulkan;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;
import org.dynamissky.vulkan.internal.gpu.SkyGpuBackendAdapter;
import org.dynamissky.vulkan.lut.CameraState;
import org.dynamissky.vulkan.lut.GpuImage2DAlloc;
import org.dynamissky.vulkan.lut.GpuImage3DAlloc;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.junit.jupiter.api.Test;

class SkyGpuAdapterSeamTest {
    @Test
    void transitionPathRoutesThroughSkyGpuAdapter() {
        TrackingAdapter adapter = new TrackingAdapter();
        VulkanSkyService service = VulkanSkyService.create(0L, new InlineGpuMemoryOps(), SkyConfig.builder().build(), adapter);

        service.update(SkyFrameContext.of(0L, CameraState.defaultState(), 0, 1f));

        assertTrue(adapter.prepareCalled);
        assertTrue(adapter.transitionHandles.size() >= 1);
    }

    private static final class TrackingAdapter implements SkyGpuBackendAdapter {
        private boolean prepareCalled;
        private final List<Long> transitionHandles = new ArrayList<>();

        @Override
        public void prepareFrame(final SkyFrameContext frameContext) {
            this.prepareCalled = true;
        }

        @Override
        public void transitionToShaderRead(final long imageHandle) {
            this.transitionHandles.add(imageHandle);
        }
    }

    private static final class InlineGpuMemoryOps implements GpuMemoryOps {
        private final AtomicLong ids = new AtomicLong(1L);

        @Override
        public GpuImage2DAlloc createImage2D(int width, int height, int format) {
            long id = ids.getAndIncrement();
            return new GpuImage2DAlloc(new GpuImage2D(id, width, height), id + 10_000L, format);
        }

        @Override
        public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
            long id = ids.getAndIncrement();
            return new GpuImage3DAlloc(new GpuImage3D(id, width, height, depth), id + 10_000L, format);
        }

        @Override
        public void destroyImage(long imageHandle, long memoryHandle) {
            // no-op test backend
        }
    }
}
