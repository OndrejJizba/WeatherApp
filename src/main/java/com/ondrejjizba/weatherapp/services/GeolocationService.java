package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;

import java.io.IOException;
import java.util.List;

public interface GeolocationService {
    String fetchGeolocationData(String cityName) throws IOException;
    List<GeolocationData> processGeolocationData(String response) throws JsonProcessingException;
}
