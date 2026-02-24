package org.dynamissky.bench.core;

import org.dynamissky.core.stars.StarCatalog;
import org.dynamissky.core.stars.StarCatalogLoader;
import org.dynamissky.vulkan.stars.StarFieldVertex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class StarCatalogBenchmark {

    @Param({"100", "1000", "9100"})
    public int starCount;

    private Path catalogPath;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        catalogPath = Files.createTempFile("dynamissky-stars-", ".bin");
        byte[] data = new byte[Math.max(0, starCount) * StarFieldVertex.STRIDE];
        Files.write(catalogPath, data);
    }

    @TearDown(Level.Trial)
    public void tearDown() throws IOException {
        if (catalogPath != null) {
            Files.deleteIfExists(catalogPath);
        }
    }

    @Benchmark
    public StarCatalog loadAndFilter(Blackhole bh) {
        StarCatalog catalog = StarCatalogLoader.load(catalogPath);
        bh.consume(catalog);
        return catalog;
    }

    @Benchmark
    public int packForGpu(Blackhole bh) {
        StarCatalog catalog = StarCatalogLoader.load(catalogPath);
        ByteBuffer buffer = ByteBuffer.allocate(Math.max(0, catalog.count() * StarFieldVertex.STRIDE));
        for (int i = 0; i < catalog.count(); i++) {
            float f = i;
            new StarFieldVertex(1f, 0f, 0f, 6.5f - (f % 5f), 1f, 1f, 1f, 0f).pack(buffer);
        }
        bh.consume(buffer);
        return catalog.count();
    }
}
