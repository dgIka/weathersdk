package org.weather;

/**
 * Defines how the WeatherClient updates weather data.
 */
public enum Mode {
    /**
     * Data is fetched from OpenWeather only when requested.
     */
    ON_DEMAND,

    /**
     * Weather data for cached cities is refreshed periodically in the background.
     */
    POLLING
}
