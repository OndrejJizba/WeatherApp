package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.WeatherEntity;

import java.io.IOException;

public interface WeatherService {
    String fetchData(String lat, String lon) throws IOException;
    WeatherEntity processWeatherData(String response) throws JsonProcessingException;
}
