package org.dynamissky.test.harness;

import org.dynamissky.test.assertions.SkyAssertions;
import org.dynamissky.test.mock.MockSkyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeOfDaySimHarnessTest {
    @Test
    void simAdvancesTimeMonotonically() {
        TimeOfDaySimHarness.SimResult result = TimeOfDaySimHarness.builder()
                .service(new MockSkyService())
                .steps(1440)
                .deltaSeconds(60f)
                .timeMultiplier(1f)
                .run();
        SkyAssertions.assertTimeAdvancesMonotonically(result);
    }

    @Test
    void simColorTempPeaksAtNoon() {
        TimeOfDaySimHarness.SimResult result = TimeOfDaySimHarness.builder()
                .service(new MockSkyService())
                .steps(1440)
                .deltaSeconds(60f)
                .timeMultiplier(1f)
                .run();
        SkyAssertions.assertColorTempPeaksAtNoon(result);
    }

    @Test
    void simStarVisibilityZeroAtNoon() {
        TimeOfDaySimHarness.SimResult result = TimeOfDaySimHarness.builder()
                .service(new MockSkyService())
                .steps(721)
                .deltaSeconds(60f)
                .timeMultiplier(1f)
                .run();
        assertEquals(0.0f, SkyAssertions.starVisibility(result.stepAt(720)));
    }

    @Test
    void simStarVisibilityOneAtMidnight() {
        TimeOfDaySimHarness.SimResult result = TimeOfDaySimHarness.builder()
                .service(new MockSkyService())
                .steps(1440)
                .deltaSeconds(60f)
                .timeMultiplier(1f)
                .run();
        assertEquals(1.0f, SkyAssertions.starVisibility(result.stepAt(0)));
        assertEquals(1.0f, SkyAssertions.starVisibility(result.stepAt(1439)));
    }
}
