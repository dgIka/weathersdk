package org.weather.demo;

import org.weather.Mode;
import org.weather.WeatherClient;
import org.weather.WeatherClientFactory;
import org.weather.WeatherSdkException;

public class Main {
    public static void main(String[] args) {
        String apiKey = "f47f306711763bf399af17bb6063209a";

        WeatherClient client = WeatherClientFactory.getClient(apiKey, Mode.ON_DEMAND);

        try {
            var weather = client.getCurrentWeather("London");
            System.out.println("City: " + weather.name);
            System.out.println("Desc: " + weather.weather.description);
        } catch (WeatherSdkException e) {
            e.printStackTrace();
        } finally {
            WeatherClientFactory.deleteClient(apiKey);
        }
    }
}
