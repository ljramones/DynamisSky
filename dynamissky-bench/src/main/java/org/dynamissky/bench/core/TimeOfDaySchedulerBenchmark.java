package org.dynamissky.bench.core;

import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.core.scheduler.TimeOfDayScheduler;
import org.dynamissky.core.solar.JulianDate;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class TimeOfDaySchedulerBenchmark {

    @Param({"1.0", "60.0", "3600.0"})
    public double timeMultiplier;

    private TimeOfDayScheduler scheduler;

    @Setup
    public void setup() {
        scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(JulianDate.of(2024, 6, 21, 0, 0, 0).value())
                .timeZone(-4.0)
                .timeMultiplier(timeMultiplier)
                .build();
    }

    @Benchmark
    public TimeOfDayState advanceOneTick(Blackhole bh) {
        TimeOfDayState state = scheduler.advance(1.0 / 60.0, 25.0);
        bh.consume(state);
        return state;
    }
}
