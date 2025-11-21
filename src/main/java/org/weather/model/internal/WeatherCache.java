package org.weather.model.internal;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherCache {

    private final long maxAgeMillis;

    private static final int MAX_CITIES = 10;

    private final Map<String, CacheEntry> cache =
            new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_CITIES;
                }
            };

    public WeatherCache(long maxAgeMillis) {
        this.maxAgeMillis = maxAgeMillis;
    }

    public synchronized CacheEntry get(String city) {
        return cache.get(city.toLowerCase());
    }

    public synchronized void put(String city, CacheEntry entry) {
        cache.put(city.toLowerCase(), entry);
    }

    public long getMaxAgeMillis() {
        return maxAgeMillis;
    }

    public synchronized Map<String, CacheEntry> snapshot() {
        return Map.copyOf(cache);
    }
}
