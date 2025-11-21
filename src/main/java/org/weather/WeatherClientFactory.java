package org.weather;

import java.util.HashMap;
import java.util.Map;

public final class WeatherClientFactory {

    private static final Map<String, WeatherClient> CLIENTS = new HashMap<>();

    private WeatherClientFactory() {
    }

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

    public static synchronized void deleteClient(String apiKey) {
        WeatherClient client = CLIENTS.remove(apiKey);
        if (client != null) {
            client.shutdown();
        }
    }
}
