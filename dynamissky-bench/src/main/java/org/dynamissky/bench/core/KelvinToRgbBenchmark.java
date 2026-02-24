package org.dynamissky.bench.core;

import org.dynamissky.core.color.KelvinToRgb;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.vectrix.core.Vector3f;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class KelvinToRgbBenchmark {

    @Param({"1800", "3200", "5500", "6500"})
    public float kelvin;

    private final Vector3f scratch = new Vector3f();

    @Benchmark
    public Vector3f convert(Blackhole bh) {
        Vector3f result = KelvinToRgb.toLinearRgb(kelvin, scratch);
        bh.consume(result);
        return result;
    }
}
