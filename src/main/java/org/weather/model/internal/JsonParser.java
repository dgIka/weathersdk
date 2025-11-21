package org.weather.model.internal;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.weather.WeatherSdkException;
import org.weather.model.*;

public class JsonParser {

    private final ObjectMapper mapper = new ObjectMapper();

    public WeatherResponse parse(String json) {
        if (json == null || json.isBlank()) {
            throw new WeatherSdkException("Empty JSON response from OpenWeather API");
        }

        try {
            JsonNode root = mapper.readTree(json);

            WeatherResponse resp = new WeatherResponse();


            JsonNode weatherArray = root.path("weather");
            if (weatherArray.isArray() && weatherArray.size() > 0) {
                JsonNode wNode = weatherArray.get(0);
                Weather w = new Weather();
                w.main = wNode.path("main").asText(null);
                w.description = wNode.path("description").asText(null);
                resp.weather = w;
            }


            JsonNode mainNode = root.path("main");
            if (!mainNode.isMissingNode()) {
                Temperature t = new Temperature();
                t.temp = mainNode.path("temp").asDouble();
                t.feels_like = mainNode.path("feels_like").asDouble();
                resp.temperature = t;
            }


            resp.visibility = root.path("visibility").asInt(0);


            JsonNode windNode = root.path("wind");
            if (!windNode.isMissingNode()) {
                Wind wind = new Wind();
                wind.speed = windNode.path("speed").asDouble();
                resp.wind = wind;
            }

            resp.datetime = root.path("dt").asLong(0);


            JsonNode sysNode = root.path("sys");
            if (!sysNode.isMissingNode()) {
                Sys sys = new Sys();
                sys.sunrise = sysNode.path("sunrise").asLong(0);
                sys.sunset = sysNode.path("sunset").asLong(0);
                resp.sys = sys;
            }


            resp.timezone = root.path("timezone").asInt(0);
            resp.name = root.path("name").asText(null);

            return resp;
        } catch (JsonProcessingException e) {
            throw new WeatherSdkException("Failed to parse OpenWeather API response", e);
        }
    }
}
