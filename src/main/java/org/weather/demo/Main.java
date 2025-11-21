package org.weather.demo;

import org.weather.Mode;
import org.weather.WeatherClient;
import org.weather.WeatherClientFactory;
import org.weather.WeatherSdkException;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String apiKey = "f47f306711763bf399af17bb6063209a";

        WeatherClient client = WeatherClientFactory.getClient(apiKey, Mode.POLLING);

        for (int i = 0; i < 5; i++) {
            var w = client.getCurrentWeather("London");
            System.out.println("Temp = " + w.temperature.temp);
            Thread.sleep(5000);
        }
    }
}
