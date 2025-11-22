package org.weather;

/**
 * Base exception type for all errors produced by the Weather SDK.
 */
public class WeatherSdkException extends RuntimeException {

    public WeatherSdkException(String message) {
        super(message);
    }

    public WeatherSdkException(String message, Throwable cause) {
        super(message, cause);
    }
}
