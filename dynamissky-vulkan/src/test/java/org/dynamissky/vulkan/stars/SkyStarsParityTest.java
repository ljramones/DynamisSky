package org.dynamissky.vulkan.stars;

import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;
import org.dynamissky.core.stars.StarCatalog;
import org.dynamissky.vulkan.lut.GpuImage2DAlloc;
import org.dynamissky.vulkan.lut.GpuImage3DAlloc;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.junit.jupiter.api.Test;
import org.vectrix.core.Matrix4f;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SkyStarsParityTest {

    @Test
    void starFieldPipelineCreatesWithoutError() {
        StarFieldRenderer renderer = StarFieldRenderer.create(1L, 2L, new FakeGpuMemoryOps());
        assertNotEquals(0L, renderer.pipelineHandle());
    }

    @Test
    void starFieldVertexBufferUploadsNonZeroStarCount() {
        StarFieldRenderer renderer = StarFieldRenderer.create(1L, 2L, new FakeGpuMemoryOps());
        renderer.uploadCatalog(new StarCatalog(10), 0L);

        assertEquals(10, renderer.uploadedStarCount());
        assertEquals(10 * StarFieldVertex.STRIDE, renderer.uploadedBytes());
    }

    @Test
    void starFieldRecordDoesNotThrowAtFullVisibility() {
        StarFieldRenderer renderer = StarFieldRenderer.create(1L, 2L, new FakeGpuMemoryOps());
        renderer.record(0L, StarPassUbo.of(new Matrix4f().identity(), 1.0f, 1.0f, 0.1f), 1.0f, 0);
        assertEquals(1, renderer.recordCount());
    }

    @Test
    void starFieldRecordDoesNotThrowAtZeroVisibility() {
        StarFieldRenderer renderer = StarFieldRenderer.create(1L, 2L, new FakeGpuMemoryOps());
        renderer.record(0L, StarPassUbo.of(new Matrix4f().identity(), 0.0f, 1.0f, 0.1f), 0.0f, 0);
        assertEquals(1, renderer.recordCount());
    }

    private static final class FakeGpuMemoryOps implements GpuMemoryOps {
        private final AtomicLong ids = new AtomicLong(1);

        @Override
        public GpuImage2DAlloc createImage2D(int width, int height, int format) {
            long image = ids.getAndIncrement();
            long memory = ids.getAndIncrement();
            return new GpuImage2DAlloc(new GpuImage2D(image, width, height), memory, format);
        }

        @Override
        public GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
            long image = ids.getAndIncrement();
            long memory = ids.getAndIncrement();
            return new GpuImage3DAlloc(new GpuImage3D(image, width, height, depth), memory, format);
        }

        @Override
        public void destroyImage(long imageHandle, long memoryHandle) {
            // no-op
        }
    }
}
