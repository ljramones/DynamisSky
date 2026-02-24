package org.dynamissky.bench.core;

import org.dynamissky.bench.BenchmarkFixtures;
import org.dynamissky.core.model.HosekWilkieSkyModel;
import org.dynamissky.core.model.PreethamSkyModel;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.vectrix.core.Vector3f;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class SkyModelBenchmark {

    @Param({"2.0", "5.0", "10.0"})
    public float turbidity;

    private PreethamSkyModel preetham;
    private HosekWilkieSkyModel hosekWilkie;
    private Vector3f viewDir;
    private Vector3f sunDir;

    @Setup
    public void setup() {
        preetham = new PreethamSkyModel(turbidity);
        hosekWilkie = new HosekWilkieSkyModel(turbidity);
        viewDir = new Vector3f(BenchmarkFixtures.ZENITH);
        sunDir = new Vector3f(0.3f, 0.7f, 0.2f).normalize();
    }

    @Benchmark
    public Vector3f preethamEvaluate(Blackhole bh) {
        Vector3f result = preetham.evaluate(viewDir, sunDir);
        bh.consume(result);
        return result;
    }

    @Benchmark
    public Vector3f hosekWilkieEvaluate(Blackhole bh) {
        Vector3f result = hosekWilkie.evaluate(viewDir, sunDir);
        bh.consume(result);
        return result;
    }
}
