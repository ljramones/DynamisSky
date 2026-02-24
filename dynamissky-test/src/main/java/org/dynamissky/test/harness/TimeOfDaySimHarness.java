package org.dynamissky.test.harness;

import org.dynamissky.api.service.SkyService;
import org.dynamissky.api.state.SunState;
import org.dynamissky.api.state.TimeOfDayState;
import org.dynamissky.api.state.WeatherState;
import org.dynamissky.test.mock.MockSkyService;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixed-step simulation harness for time-of-day behavior tests.
 */
public final class TimeOfDaySimHarness {
    private TimeOfDaySimHarness() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private SkyService service;
        private int steps = 1;
        private float deltaSeconds = 1f;
        private double startJulianDate = 2451545.0;
        private float timeMultiplier = 1f;

        public Builder service(SkyService value) {
            this.service = value;
            return this;
        }

        public Builder steps(int value) {
            this.steps = value;
            return this;
        }

        public Builder deltaSeconds(float value) {
            this.deltaSeconds = value;
            return this;
        }

        public Builder startJulianDate(double value) {
            this.startJulianDate = value;
            return this;
        }

        public Builder timeMultiplier(float value) {
            this.timeMultiplier = value;
            return this;
        }

        public SimResult run() {
            if (service == null) {
                throw new IllegalStateException("service is required");
            }
            service.setTimeOfDayHours(0.0);
            service.setTimeMultiplier(timeMultiplier);

            if (service instanceof MockSkyService mock) {
                TimeOfDayState current = mock.timeOfDayState();
                mock.withTimeOfDay(new TimeOfDayState(
                        startJulianDate,
                        current.localTimeHours(),
                        current.timeMultiplier(),
                        current.locked(),
                        current.ambientIntensity(),
                        current.colorTemperatureKelvin()));
            }

            List<TimeOfDayState> times = new ArrayList<>();
            List<SunState> suns = new ArrayList<>();
            List<WeatherState> weather = new ArrayList<>();

            for (int i = 0; i < steps; i++) {
                times.add(service.timeOfDayState());
                suns.add(service.sunState());
                weather.add(service.weatherState());
                if (service instanceof MockSkyService mock) {
                    mock.advance(deltaSeconds);
                }
            }

            return new SimResult(times, suns, weather, steps);
        }
    }

    public record SimResult(
            List<TimeOfDayState> timeStates,
            List<SunState> sunStates,
            List<WeatherState> weatherStates,
            int totalSteps) {

        public TimeOfDayState stepAt(int index) {
            return timeStates.get(index);
        }

        public SunState sunAt(int index) {
            return sunStates.get(index);
        }

        public boolean sunRisesInWindow() {
            for (int i = 1; i < sunStates.size(); i++) {
                if (sunStates.get(i - 1).altitudeDegrees() <= 0.0 && sunStates.get(i).altitudeDegrees() > 0.0) {
                    return true;
                }
            }
            return false;
        }

        public boolean sunSetsInWindow() {
            for (int i = 1; i < sunStates.size(); i++) {
                if (sunStates.get(i - 1).altitudeDegrees() > 0.0 && sunStates.get(i).altitudeDegrees() <= 0.0) {
                    return true;
                }
            }
            return false;
        }
    }
}
