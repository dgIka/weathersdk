package org.weather;


import org.weather.model.WeatherResponse;
import org.weather.model.internal.CacheEntry;
import org.weather.model.internal.HttpService;
import org.weather.model.internal.JsonParser;
import org.weather.model.internal.WeatherCache;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherClient {

    private static final long CACHE_MAX_AGE_MILLIS = 10 * 60 * 1000L;     // 10 минут
    private static final long POLLING_INTERVAL_MILLIS = 60_000L;          // 1 минута

    private final String apiKey;
    private final Mode mode;

    private final WeatherCache cache = new WeatherCache(CACHE_MAX_AGE_MILLIS);
    private final HttpService httpService = new HttpService();
    private final JsonParser jsonParser = new JsonParser();

    private ScheduledExecutorService scheduler;

    private WeatherClient(String apiKey, Mode mode) {
        this.apiKey = apiKey;
        this.mode = mode;

        if (mode == Mode.POLLING) {
            startPolling();
        }
    }

    static WeatherClient create(String apiKey, Mode mode) {
        return new WeatherClient(apiKey, mode);
    }

    public WeatherResponse getCurrentWeather(String cityName) {
        if (cityName == null || cityName.isBlank()) {
            throw new WeatherSdkException("City name must not be null or blank");
        }

        String cityKey = cityName.trim();
        long now = System.currentTimeMillis();


        CacheEntry entry = cache.get(cityKey);
        if (entry != null && (now - entry.timestampMillis) < cache.getMaxAgeMillis()) {
            return entry.response;
        }


        String json = httpService.fetchWeatherJson(apiKey, cityKey);
        WeatherResponse response = jsonParser.parse(json);


        CacheEntry newEntry = new CacheEntry();
        newEntry.response = response;
        newEntry.timestampMillis = now;
        cache.put(cityKey, newEntry);

        return response;
    }

    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                this::refreshAllCities,
                POLLING_INTERVAL_MILLIS,
                POLLING_INTERVAL_MILLIS,
                TimeUnit.MILLISECONDS
        );
    }

    private void refreshAllCities() {
        Map<String, CacheEntry> snapshot = cache.snapshot();
        for (String city : snapshot.keySet()) {
            try {
                String json = httpService.fetchWeatherJson(apiKey, city);
                WeatherResponse response = jsonParser.parse(json);

                CacheEntry entry = new CacheEntry();
                entry.response = response;
                entry.timestampMillis = System.currentTimeMillis();
                cache.put(city, entry);

            } catch (Exception ignored) {
            }
        }
    }

    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    public String getApiKey() {
        return apiKey;
    }

    public Mode getMode() {
        return mode;
    }
}

