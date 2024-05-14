package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public String fetchData(String lat, String lon) throws IOException {
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
}
