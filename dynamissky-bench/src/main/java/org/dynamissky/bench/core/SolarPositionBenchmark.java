package org.dynamissky.bench.core;

import org.dynamissky.core.solar.JulianDate;
import org.dynamissky.core.solar.LatLon;
import org.dynamissky.core.solar.SolarPosition;
import org.dynamissky.core.solar.SolarPositionCalculator;
import org.dynamissky.core.solar.TimeZoneOffset;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class SolarPositionBenchmark {

    private JulianDate julianDate;
    private LatLon toronto;
    private TimeZoneOffset est;

    @Setup
    public void setup() {
        julianDate = JulianDate.of(2024, 6, 21, 12, 0, 0);
        toronto = new LatLon(43.7, -79.4);
        est = new TimeZoneOffset(-4f);
    }

    @Benchmark
    public SolarPosition computePosition() {
        return SolarPositionCalculator.compute(julianDate, toronto, est);
    }

    @Benchmark
    public JulianDate julianDateConstruction() {
        return JulianDate.of(2024, 6, 21, 12, 0, 0);
    }
}
