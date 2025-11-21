package org.weather.model.internal;

import org.weather.WeatherSdkException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class HttpService {

    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather";

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public String fetchWeatherJson(String apiKey, String cityName) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new WeatherSdkException("API key must not be null or blank");
        }
        if (cityName == null || cityName.isBlank()) {
            throw new WeatherSdkException("City name must not be null or blank");
        }

        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        String url = BASE_URL + "?q=" + encodedCity + "&appid=" + apiKey;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WeatherSdkException("Failed to call OpenWeather API", e);
        }

        int status = response.statusCode();
        if (status != 200) {
            throw new WeatherSdkException(
                    "OpenWeather API returned status " + status + ": " + response.body());
        }

        return response.body();
    }
}
