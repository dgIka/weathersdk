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

/**
 * Main SDK client for accessing the OpenWeather API.
 * <p>
 * Supports two modes:
 * <ul>
 *   <li>ON_DEMAND — fetches data only on request</li>
 *   <li>POLLING — periodically refreshes cached cities in the background</li>
 * </ul>
 * Instances must be created through {@link WeatherClientFactory}.
 */
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

    /**
     * Returns current weather for the given city.
     * <p>
     * Uses cached value if it is not older than the configured max age;
     * otherwise calls OpenWeather API and updates the cache.
     *
     * @param cityName city name to search for (the first matching city is used)
     * @return normalized weather data for the city
     * @throws WeatherSdkException if cityName is invalid, the API call fails, or the response cannot be parsed
     */
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

    /**
     * Stops background polling (if enabled) and releases all internal resources.
     * After calling this method the client should not be used anymore.
     */
    public void shutdown() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    /**
     * @return the OpenWeather API key used by this client
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * @return the operating mode of this client (ON_DEMAND or POLLING)
     */
    public Mode getMode() {
        return mode;
    }
}

