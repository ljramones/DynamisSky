package org.dynamissky.vulkan.pass;

import org.dynamissky.api.descriptor.SkyModelType;
import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;
import org.dynamissky.vulkan.descriptor.SkyDescriptorSets;
import org.dynamissky.vulkan.lut.GpuImage2DAlloc;
import org.dynamissky.vulkan.lut.GpuImage3DAlloc;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.dynamissky.vulkan.lut.SkyLutResources;
import org.junit.jupiter.api.Test;
import org.vectrix.core.Matrix4f;
import org.vectrix.core.Vector3f;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkyBackgroundParityTest {

    @Test
    void skyBackgroundPipelineCreatesWithoutError() {
        FakeGpuMemoryOps memoryOps = new FakeGpuMemoryOps();
        SkyLutResources luts = SkyLutResources.create(memoryOps);
        SkyDescriptorSets sets = SkyDescriptorSets.create(luts);

        SkyBackgroundPass pass = SkyBackgroundPass.create(1L, 2L, luts, sets);

        assertNotEquals(0L, pass.pipelineHandle());
    }

    @Test
    void hdriPassLoadsTextureWithoutError() {
        FakeGpuMemoryOps memoryOps = new FakeGpuMemoryOps();
        SkyLutResources luts = SkyLutResources.create(memoryOps);
        SkyDescriptorSets sets = SkyDescriptorSets.create(luts);

        HdriSkyPass pass = HdriSkyPass.create(1L, 2L, memoryOps, sets);
        pass.loadHdri(new float[]{
                1f, 0f, 0f, 1f,
                0f, 1f, 0f, 1f,
                0f, 0f, 1f, 1f,
                1f, 1f, 1f, 1f
        }, 2, 2, 0L);

        assertTrue(pass.hdriLoaded());
        assertEquals(2, pass.hdriWidth());
        assertEquals(2, pass.hdriHeight());
    }

    @Test
    void skyPassSelectorRoutesCorrectly() {
        FakeGpuMemoryOps memoryOps = new FakeGpuMemoryOps();
        SkyLutResources luts = SkyLutResources.create(memoryOps);
        SkyDescriptorSets sets = SkyDescriptorSets.create(luts);

        SkyBackgroundPass bgPass = SkyBackgroundPass.create(1L, 2L, luts, sets);
        HdriSkyPass hdriPass = HdriSkyPass.create(1L, 2L, memoryOps, sets);
        SkyPassSelector selector = new SkyPassSelector(bgPass, hdriPass);

        SkyPassUbo ubo = SkyPassUbo.of(new Matrix4f().identity(), new Vector3f(0f, 1f, 0f));

        selector.record(0L, ubo, SkyModelType.BRUNETON, 0f, 1f, 0);
        selector.record(0L, ubo, SkyModelType.HDRI, 0f, 1f, 0);

        assertEquals(1, selector.backgroundRecordCount());
        assertEquals(1, selector.hdriRecordCount());
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
