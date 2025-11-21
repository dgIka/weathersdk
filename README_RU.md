# WeatherSDK

Небольшой Java SDK для доступа к OpenWeather API (текущая погода по названию города) с кэшем и двумя режимами работы: **ON_DEMAND** и **POLLING**.

## Возможности

- Инициализация по API key.
- Получение текущей погоды по названию города.
- Кэш до 10 городов, данные считаются актуальными 10 минут.
- Два режима:
    - `ON_DEMAND` — запрос к API только по вызову клиента.
    - `POLLING` — периодическое обновление кэша в фоне.
- Один `WeatherClient` на API key (через `WeatherClientFactory`).
- Все ошибки — через `WeatherSdkException`.

## Установка

Локально:

```bash
mvn clean package
```

Получится jar, например: `target/WeatherSDK-1.0-SNAPSHOT.jar`.



SDK зависит от:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.0</version>
</dependency>
```

## Основные классы

Публичное API (пакет `org.weather`):

- `WeatherClient` — основной клиент.
- `WeatherClientFactory` — получение/удаление клиента по API key.
- `WeatherSdkException` — исключения SDK.
- `Mode` — режим работы (`ON_DEMAND`, `POLLING`).

Модели ответа (пакет `org.weather.model`):

- `WeatherResponse`
- `Weather`
- `Temperature`
- `Wind`
- `Sys`


## Использование

### Инициализация клиента (ON_DEMAND)

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

### Инициализация клиента (POLLING)

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

## Жизненный цикл клиента

- Создание: `WeatherClientFactory.getClient(apiKey, mode)`
- Завершение: `WeatherClientFactory.deleteClient(apiKey)`
