package org.dynamissky.core.scheduler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeOfDaySchedulerTest {
    @Test
    void starVisibilityBlendsAcrossTwilightBand() {
        assertEquals(1.0f, TimeOfDayScheduler.starVisibilityForSunAltitude(-6.0), 1e-6f);
        assertEquals(0.5f, TimeOfDayScheduler.starVisibilityForSunAltitude(0.0), 1e-6f);
        assertEquals(0.0f, TimeOfDayScheduler.starVisibilityForSunAltitude(6.0), 1e-6f);
    }
}
