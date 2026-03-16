package org.dynamisengine.sky.core.scheduler;

import org.dynamisengine.sky.api.state.TimeOfDayState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimeOfDaySchedulerTest {

    private static final double J2000 = 2451545.0;

    @Test
    void starVisibilityBlendsAcrossTwilightBand() {
        assertEquals(1.0f, TimeOfDayScheduler.starVisibilityForSunAltitude(-6.0), 1e-6f);
        assertEquals(0.5f, TimeOfDayScheduler.starVisibilityForSunAltitude(0.0), 1e-6f);
        assertEquals(0.0f, TimeOfDayScheduler.starVisibilityForSunAltitude(6.0), 1e-6f);
    }

    @Test
    void advanceProgressesTimeCorrectlyAtNormalSpeed() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        TimeOfDayState before = scheduler.advance(0.0, 45.0);
        TimeOfDayState after = scheduler.advance(3600.0, 45.0);

        // 3600s at 1x = 1 hour = 1/24 day
        assertEquals(1.0 / 24.0, after.julianDate() - before.julianDate(), 1e-9);
    }

    @Test
    void advanceWithTimeMultiplierSpeedsUpProgression() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(60.0)
                .timeZone(0.0)
                .build();

        TimeOfDayState before = scheduler.advance(0.0, 45.0);
        // 60 real seconds at 60x = 3600 game seconds = 1 hour
        TimeOfDayState after = scheduler.advance(60.0, 45.0);

        assertEquals(1.0 / 24.0, after.julianDate() - before.julianDate(), 1e-9);
    }

    @Test
    void setTimeOfDayJumpsToSpecifiedHour() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        scheduler.setTimeOfDay(14.5);
        TimeOfDayState state = scheduler.advance(0.0, 45.0);

        assertEquals(14.5, state.localTimeHours(), 1e-4);
    }

    @Test
    void lockFreezesTimeAndUnlockResumesIt() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        TimeOfDayState initial = scheduler.advance(0.0, 45.0);
        double jdInitial = initial.julianDate();

        scheduler.lock();
        TimeOfDayState locked = scheduler.advance(7200.0, 45.0);
        assertEquals(jdInitial, locked.julianDate(), 0.0);
        assertTrue(locked.locked());

        scheduler.unlock();
        TimeOfDayState unlocked = scheduler.advance(3600.0, 45.0);
        assertTrue(unlocked.julianDate() > jdInitial);
        assertFalse(unlocked.locked());
    }

    @Test
    void colorTemperatureWarmAtSunriseCoolAtNoon() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        scheduler.setTimeOfDay(6.0);
        int sunriseKelvin = scheduler.advance(0.0, 0.0).colorTemperatureKelvin();

        scheduler.setTimeOfDay(12.0);
        int noonKelvin = scheduler.advance(0.0, 60.0).colorTemperatureKelvin();

        scheduler.setTimeOfDay(19.0);
        int sunsetKelvin = scheduler.advance(0.0, 0.0).colorTemperatureKelvin();

        assertTrue(noonKelvin > sunriseKelvin,
                "Noon (" + noonKelvin + "K) should be cooler than sunrise (" + sunriseKelvin + "K)");
        assertTrue(noonKelvin > sunsetKelvin,
                "Noon (" + noonKelvin + "K) should be cooler than sunset (" + sunsetKelvin + "K)");
    }

    @Test
    void ambientLowAtNightHighAtDay() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        // Sun well below horizon
        float nightAmbient = scheduler.advance(0.0, -30.0).ambientIntensity();
        // Sun high in sky
        float dayAmbient = scheduler.advance(0.0, 60.0).ambientIntensity();

        assertTrue(dayAmbient > nightAmbient);
        assertEquals(0.02f, nightAmbient, 0.01f);
        assertEquals(1.0f, dayAmbient, 0.001f);
    }

    @Test
    void multipleAdvanceCallsAccumulateCorrectly() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        TimeOfDayState start = scheduler.advance(0.0, 45.0);

        // 10 advances of 360s = 3600s total = 1 hour
        for (int i = 0; i < 10; i++) {
            scheduler.advance(360.0, 45.0);
        }

        TimeOfDayState end = scheduler.advance(0.0, 45.0);
        assertEquals(1.0 / 24.0, end.julianDate() - start.julianDate(), 1e-4);
    }

    @Test
    void timeWrapsCorrectlyPastMidnight() {
        TimeOfDayScheduler scheduler = TimeOfDayScheduler.builder()
                .startJulianDate(J2000)
                .timeMultiplier(1.0)
                .timeZone(0.0)
                .build();

        scheduler.setTimeOfDay(23.5);
        TimeOfDayState before = scheduler.advance(0.0, -10.0);
        assertEquals(23.5, before.localTimeHours(), 1e-4);

        // Advance 1 hour → should wrap to ~0.5
        TimeOfDayState after = scheduler.advance(3600.0, -10.0);
        assertEquals(0.5, after.localTimeHours(), 1e-6);
    }
}
