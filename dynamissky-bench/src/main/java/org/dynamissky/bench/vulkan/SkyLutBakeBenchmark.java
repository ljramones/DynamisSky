package org.dynamissky.bench.vulkan;

import org.dynamissky.api.config.AtmosphereConfig;
import org.dynamissky.api.gpu.GpuImage2D;
import org.dynamissky.api.gpu.GpuImage3D;
import org.dynamissky.vulkan.lut.GpuImage2DAlloc;
import org.dynamissky.vulkan.lut.GpuImage3DAlloc;
import org.dynamissky.vulkan.lut.GpuMemoryOps;
import org.dynamissky.vulkan.lut.SkyLutReadbackRegistry;
import org.dynamissky.vulkan.lut.SkyLutResources;
import org.dynamissky.vulkan.lut.TransmittanceLutBaker;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class SkyLutBakeBenchmark {

    private SkyLutResources luts;
    private TransmittanceLutBaker baker;

    @Setup(Level.Trial)
    public void setup() {
        GpuMemoryOps memoryOps = new BenchFakeGpuMemoryOps();
        luts = SkyLutResources.create(memoryOps);
        baker = TransmittanceLutBaker.create(1L, memoryOps, luts);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        baker.destroy();
        luts.destroy();
    }

    @Benchmark
    public void transmittanceBakeCost(Blackhole bh) {
        baker.bake(1L, AtmosphereConfig.EARTH_STANDARD, 0);
        bh.consume(SkyLutReadbackRegistry.readCenterPixel(luts.transmittanceLut().handle()));
    }

    private static final class BenchFakeGpuMemoryOps implements GpuMemoryOps {
        private final AtomicLong ids = new AtomicLong(1L);

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
