# WeatherSDK

A lightweight Java SDK for accessing the OpenWeather API (current weather by city name) with caching and two operating modes: **ON_DEMAND** and **POLLING**.

## Features

- Initialization using an API key.
- Fetching current weather by city name.
- Cache for up to 10 cities, data considered fresh for 10 minutes.
- Two modes:
    - `ON_DEMAND` — fetches from API only when requested.
    - `POLLING` — periodically refreshes cached cities in the background.
- One `WeatherClient` per API key (managed by `WeatherClientFactory`).
- All errors thrown as `WeatherSdkException`.

## Installation

Local build:

```bash
mvn clean package
```

This will produce a jar file, e.g.: `target/WeatherSDK-1.0-SNAPSHOT.jar`.

The SDK depends on:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version>
</dependency>
```

## Main classes

Public API (package `org.weather`):

- `WeatherClient` — main client.
- `WeatherClientFactory` — creates/deletes clients by API key.
- `WeatherSdkException` — custom SDK exceptions.
- `Mode` — defines operation mode (`ON_DEMAND`, `POLLING`).

Response models (package `org.weather.model`):

- `WeatherResponse`
- `Weather`
- `Temperature`
- `Wind`
- `Sys`

## Usage

### Client initialization (ON_DEMAND)

```java
import org.weather.*;

public class ExampleOnDemand {
    public static void main(String[] args) {
        String apiKey = "YOUR_API_KEY";

        WeatherClient client =
                WeatherClientFactory.getClient(apiKey, Mode.ON_DEMAND);

        try {
            var weather = client.getCurrentWeather("London");

            System.out.println("City: " + weather.name);
            System.out.println("Temp: " + weather.temperature.temp);
            System.out.println("Feels like: " + weather.temperature.feels_like);
            System.out.println("Weather: " + weather.weather.main + " / " + weather.weather.description);
            System.out.println("Wind speed: " + weather.wind.speed);
        } catch (WeatherSdkException e) {
            e.printStackTrace();
        } finally {
            WeatherClientFactory.deleteClient(apiKey);
        }
    }
}
```

### Client initialization (POLLING)

```java
import org.weather.*;

public class ExamplePolling {
    public static void main(String[] args) throws InterruptedException {
        String apiKey = "YOUR_API_KEY";

        WeatherClient client =
                WeatherClientFactory.getClient(apiKey, Mode.POLLING);

        try {
            client.getCurrentWeather("London");

            for (int i = 0; i < 5; i++) {
                var weather = client.getCurrentWeather("London");
                System.out.println("Temp: " + weather.temperature.temp +
                        " (iteration " + i + ")");
                Thread.sleep(5000);
            }
        } catch (WeatherSdkException e) {
            e.printStackTrace();
        } finally {
            WeatherClientFactory.deleteClient(apiKey);
        }
    }
}
```

## Client lifecycle

- Create: `WeatherClientFactory.getClient(apiKey, mode)`
- Shutdown: `WeatherClientFactory.deleteClient(apiKey)`
