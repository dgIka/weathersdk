package org.weather;

import java.util.HashMap;
import java.util.Map;


/**
 * Factory for creating and managing WeatherClient instances.
 * Ensures that only one client exists per API key.
 */
public final class WeatherClientFactory {

    private static final Map<String, WeatherClient> CLIENTS = new HashMap<>();

    private WeatherClientFactory() {
    }

    /**
     * Returns a WeatherClient for the given API key and mode.
     * <p>
     * If a client with this API key already exists, the same instance is returned.
     *
     * @param apiKey the OpenWeather API key
     * @param mode   the operating mode (ON_DEMAND or POLLING)
     * @return existing or newly created WeatherClient
     * @throws IllegalArgumentException if apiKey is null/blank or mode is null
     */
    public static synchronized WeatherClient getClient(String apiKey, Mode mode) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be null or blank");
        }
        if (mode == null) {
            throw new IllegalArgumentException("Mode must not be null");
        }

        WeatherClient existing = CLIENTS.get(apiKey);
        if (existing != null) {
            return existing;
        }

        WeatherClient client = WeatherClient.create(apiKey, mode);
        CLIENTS.put(apiKey, client);
        return client;
    }

    /**
     * Deletes the client associated with the given API key, if present.
     * Stops background polling and releases resources.
     *
     * @param apiKey the API key of the client to remove
     */
    public static synchronized void deleteClient(String apiKey) {
        WeatherClient client = CLIENTS.remove(apiKey);
        if (client != null) {
            client.shutdown();
        }
    }
}
