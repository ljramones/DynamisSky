package org.dynamisengine.sky.vulkan.moon;

import org.dynamisengine.sky.api.ColorRgb;
import org.dynamisengine.sky.api.Vec3;
import org.dynamisengine.sky.api.state.MoonState;
import org.dynamisengine.sky.api.state.SunState;
import org.dynamisengine.sky.vulkan.lut.GpuMemoryOps;
import org.junit.jupiter.api.Test;
import org.dynamisengine.vectrix.core.Matrix4f;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class SkyMoonParityTest {

    @Test
    void moonBillboardPipelineCreatesWithoutError() {
        MoonBillboardRenderer renderer = MoonBillboardRenderer.create(1L, 2L, new NoopMemoryOps(), 1L, 1L);
        assertNotEquals(0L, renderer.pipelineHandle());
    }

    @Test
    void moonBillboardRecordAtFullMoonDoesNotThrow() {
        MoonBillboardRenderer renderer = MoonBillboardRenderer.create(1L, 2L, new NoopMemoryOps(), 1L, 1L);
        MoonState moon = new MoonState(new Vec3(0f, 0.7f, -0.7f), new ColorRgb(1f, 1f, 1f), 1f, 1f, 0.53f);
        SunState sun = new SunState(new Vec3(0f, 0.7f, 0.7f), new ColorRgb(1f, 1f, 1f), 1f, 180d, 45d);

        renderer.record(0L, moon, sun, new Matrix4f().identity(), 0);
        assertEquals(1, renderer.recordCount());
    }

    @Test
    void moonBillboardRecordAtNewMoonDoesNotThrow() {
        MoonBillboardRenderer renderer = MoonBillboardRenderer.create(1L, 2L, new NoopMemoryOps(), 1L, 1L);
        MoonState moon = new MoonState(new Vec3(0f, 0.7f, -0.7f), new ColorRgb(1f, 1f, 1f), 1f, 0f, 0.53f);
        SunState sun = new SunState(new Vec3(0f, 0.7f, 0.7f), new ColorRgb(1f, 1f, 1f), 1f, 180d, 45d);

        renderer.record(0L, moon, sun, new Matrix4f().identity(), 0);
        assertEquals(1, renderer.recordCount());
    }

    private static final class NoopMemoryOps implements GpuMemoryOps {
        @Override
        public org.dynamisengine.sky.vulkan.lut.GpuImage2DAlloc createImage2D(int width, int height, int format) {
            throw new UnsupportedOperationException();
        }

        @Override
        public org.dynamisengine.sky.vulkan.lut.GpuImage3DAlloc createImage3D(int width, int height, int depth, int format) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void destroyImage(long imageHandle, long memoryHandle) {
            // no-op
        }
    }
}
