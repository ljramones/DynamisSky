package org.dynamissky.test.mock;

import org.dynamissky.api.gpu.SkyGpuResources;
import org.dynamissky.api.state.WeatherState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MockSkyServiceTest {
    @Test
    void mockReturnsConfiguredSunState() {
        MockSkyService mock = new MockSkyService().withSunState(MockSkyService.SUNSET);
        assertSame(MockSkyService.SUNSET, mock.sunState());
    }

    @Test
    void mockTracksUpdateCallCount() {
        MockSkyService mock = new MockSkyService();
        mock.advance(1f);
        mock.advance(1f);
        assertEquals(2, mock.updateCallCount());
    }

    @Test
    void mockWithWeatherUpdatesWeatherState() {
        MockSkyService mock = new MockSkyService().withWeather(WeatherState.HEAVY_RAIN);
        assertSame(WeatherState.HEAVY_RAIN, mock.weatherState());
    }

    @Test
    void mockGetGpuResourcesReturnsNull() {
        MockSkyService mock = new MockSkyService();
        assertSame(SkyGpuResources.NULL, mock.gpuResources());
    }
}
