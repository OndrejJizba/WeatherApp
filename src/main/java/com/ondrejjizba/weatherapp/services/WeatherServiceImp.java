package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.DTOs.ForecastData;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
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

    @Autowired
    public WeatherServiceImp(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Override
    public String fetchWeatherData(String lat, String lon) throws IOException {
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
        CloseableHttpClient client = HttpClients.createDefault();
        return client.execute(request, new BasicHttpClientResponseHandler());
    }

    @Override
    public WeatherEntity processWeatherData(String response) throws JsonProcessingException {
        WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);
        WeatherEntity weatherEntity = new WeatherEntity();
        weatherEntity.setName(weatherData.getName());
        weatherEntity.setDescription(weatherData.getWeather()[0].getDescription());
        weatherEntity.setTemperature(weatherData.getMain().getTemp());
        weatherEntity.setSunrise(UnixTimeConverter.converter(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
        weatherEntity.setSunset(UnixTimeConverter.converter(weatherData.getSys().getSunset(), weatherData.getTimezone()));
        weatherRepository.save(weatherEntity);
        return weatherEntity;
    }

    @Override
    public String fetchGeolocationData(String cityName) throws IOException {
        String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        HttpGet request = new HttpGet("http://api.openweathermap.org/geo/1.0/direct?q=" + encodedCityName + "&limit=5&appid=" + API_KEY);
        CloseableHttpClient client = HttpClients.createDefault();
        return client.execute(request, new BasicHttpClientResponseHandler());
    }

    @Override
    public List<GeolocationData> processGeolocationData(String response) throws JsonProcessingException {
        List<GeolocationData> geolocationData = objectMapper.readValue(response, new TypeReference<>() {});
        List<GeolocationData> geolocationResponse = new ArrayList<>();
        for (GeolocationData data : geolocationData) {
            GeolocationData geoResp = new GeolocationData();
            geoResp.setName(data.getName());
            geoResp.setLat(data.getLat());
            geoResp.setLon(data.getLon());
            geoResp.setCountry(data.getCountry());
            geolocationResponse.add(geoResp);
        }
        return geolocationResponse;
    }

    @Override
    public String fetchForecastData(String lat, String lon) throws IOException {
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/forecast?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
        CloseableHttpClient client = HttpClients.createDefault();
        return client.execute(request, new BasicHttpClientResponseHandler());
    }

    @Override
    public List<ForecastData> processForecastData(String response) throws JsonProcessingException {
        List<ForecastData> forecastResponse = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode listNode = rootNode.get("list");
        if (listNode != null && listNode.isArray()) {
            Iterator<JsonNode> elements = listNode.elements();
            while (elements.hasNext()) {
                JsonNode item = elements.next();
                ForecastData forecast = new ForecastData();
                forecast.setDt(item.get("dt").asLong());
                forecast.setTemp(item.get("main").get("temp").asDouble());
                forecast.setDescription(item.get("weather").get(0).get("description").asText());
                forecastResponse.add(forecast);
            }
        }
        return forecastResponse;
    }
}
