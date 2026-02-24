package org.dynamissky.test.assertions;

import org.dynamissky.test.mock.MockSkyService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkyAssertionsTest {
    @Test
    void assertSunAboveHorizonPassesForNoon() {
        assertDoesNotThrow(() -> SkyAssertions.assertSunAboveHorizon(MockSkyService.NOON_TORONTO));
    }

    @Test
    void assertSunAboveHorizonFailsForBelowHorizon() {
        assertThrows(AssertionError.class, () -> SkyAssertions.assertSunAboveHorizon(MockSkyService.BELOW_HORIZON));
    }

    @Test
    void assertStarsVisiblePassesAtMidnight() {
        assertDoesNotThrow(() -> SkyAssertions.assertStarsVisible(MockSkyService.MIDNIGHT));
    }

    @Test
    void assertStarsVisibleFailsAtNoon() {
        assertThrows(AssertionError.class, () -> SkyAssertions.assertStarsVisible(MockSkyService.SOLAR_NOON));
    }
}
