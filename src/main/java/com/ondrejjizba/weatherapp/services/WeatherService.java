package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.ForecastData;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import com.ondrejjizba.weatherapp.models.WeatherEntity;

import java.io.IOException;
import java.util.List;

public interface WeatherService {
    String fetchWeatherData(String lat, String lon) throws IOException;
    WeatherEntity processWeatherData(String response) throws JsonProcessingException;
    String fetchGeolocationData(String cityName) throws IOException;
    List<GeolocationData> processGeolocationData(String response) throws JsonProcessingException;
    String fetchForecastData(String lat, String lon) throws IOException;
    List<ForecastData> processForecastData(String response) throws JsonProcessingException;
}
