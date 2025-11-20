package org.weather.model;


public class WeatherClient {

    private final String apiKey;
    private final Mode mode;

    private WeatherClient(String apiKey, Mode mode) {
        this.apiKey = apiKey;
        this.mode = mode;
    }

    public static WeatherClient create(String apiKey, Mode mode) {
        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        //-----------------------------------------------------------------------------
        return new WeatherClient(apiKey, mode);
    }

    public WeatherResponse getCurrentWeather(String cityName) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void shutdown() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

