package org.weather.model.internal;

import java.util.LinkedHashMap;
import java.util.Map;

public class WeatherCache {

    private static final int MAX_CITIES = 10;
    private static final long MAX_AGE_MILLIS = 10 * 60 * 1000;

    private final Map<String, CacheEntry> cache =
            new LinkedHashMap<String, CacheEntry>(16, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    return size() > MAX_CITIES;
                }
            };

    public synchronized CacheEntry get(String city) {
        return cache.get(city.toLowerCase());
    }

    public synchronized void put(String city, CacheEntry entry) {
        cache.put(city.toLowerCase(), entry);
    }

    public long getMaxAgeMillis() {
        return MAX_AGE_MILLIS;
    }
}
