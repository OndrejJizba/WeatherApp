package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.exceptions.CityNotFoundException;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.ForecastEntity;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
import com.ondrejjizba.weatherapp.repositories.ForecastRepository;
import com.ondrejjizba.weatherapp.repositories.WeatherRepository;
import com.ondrejjizba.weatherapp.utils.UnixTimeConverter;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class WeatherServiceImp implements WeatherService{
    private static final String API_KEY = System.getenv("API_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WeatherRepository weatherRepository;
    private final ForecastRepository forecastRepository;

    @Autowired
    public WeatherServiceImp(WeatherRepository weatherRepository,
                             ForecastRepository forecastRepository) {
        this.weatherRepository = weatherRepository;
        this.forecastRepository = forecastRepository;
    }

    @Override
    public String fetchWeatherData(String lat, String lon) {
            HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
            CloseableHttpClient client = HttpClients.createDefault();
        try {
            return client.execute(request, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new CityNotFoundException("City not found for given coordinates.");
        }
    }

    @Override
    public WeatherEntity processWeatherData(String response) throws JsonProcessingException {
        WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);

        if (weatherData.getName() == null || weatherData.getName().isEmpty()) {
            throw new CityNotFoundException("City not found for given coordinates.");
        }

        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setName(weatherData.getName());
        weatherEntity.setDescription(weatherData.getWeather()[0].getDescription());
        weatherEntity.setTemperature(weatherData.getMain().getTemp());
        weatherEntity.setLat(weatherData.getCoord().getLat());
        weatherEntity.setLon(weatherData.getCoord().getLon());
        weatherEntity.setSunrise(UnixTimeConverter.converterTime(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
        weatherEntity.setSunset(UnixTimeConverter.converterTime(weatherData.getSys().getSunset(), weatherData.getTimezone()));
        String icon = weatherData.getWeather()[0].getIcon();
        weatherEntity.setIcon("https://openweathermap.org/img/wn/" + icon + ".png");
        weatherRepository.save(weatherEntity);
        return weatherEntity;
    }

    @Override
    public String fetchForecastData(String lat, String lon) throws IOException {
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/forecast?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
        CloseableHttpClient client = HttpClients.createDefault();
        return client.execute(request, new BasicHttpClientResponseHandler());
    }

    @Override
    public List<ForecastEntity> processForecastData(String response) throws JsonProcessingException {
        List<ForecastEntity> forecastResponse = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode listNode = rootNode.get("list");
        if (listNode != null && listNode.isArray()) {
            Iterator<JsonNode> elements = listNode.elements();
            while (elements.hasNext()) {
                JsonNode item = elements.next();
                ForecastEntity forecast = new ForecastEntity();
                forecast.setDt(item.get("dt").asLong());
                forecast.setTemp(item.get("main").get("temp").asDouble());
                forecast.setDescription(item.get("weather").get(0).get("description").asText());
                forecast.setTimezone(rootNode.get("city").get("timezone").asLong());
                forecast.setIcon(item.get("weather").get(0).get("icon").asText());
                forecastResponse.add(forecast);
            }
        }
        forecastRepository.saveAll(forecastResponse);
        return forecastResponse;
    }

    @Override
    public List<ForecastEntity> processForecastDataNoSave(String response) throws JsonProcessingException {
        List<ForecastEntity> forecastResponse = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode listNode = rootNode.get("list");
        if (listNode != null && listNode.isArray()) {
            Iterator<JsonNode> elements = listNode.elements();
            while (elements.hasNext()) {
                JsonNode item = elements.next();
                ForecastEntity forecast = new ForecastEntity();
                forecast.setDt(item.get("dt").asLong());
                forecast.setTemp(item.get("main").get("temp").asDouble());
                forecast.setDescription(item.get("weather").get(0).get("description").asText());
                forecast.setTimezone(rootNode.get("city").get("timezone").asLong());
                String icon = item.get("weather").get(0).get("icon").asText();
                String iconURL = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
                forecast.setIcon(iconURL);
                forecastResponse.add(forecast);
            }
        }
        return forecastResponse;
    }
}
