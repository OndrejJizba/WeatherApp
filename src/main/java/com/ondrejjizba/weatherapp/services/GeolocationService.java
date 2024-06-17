package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;

import java.io.IOException;
import java.util.List;

public interface GeolocationService {
    String fetchGeolocationData(String cityName);
    List<GeolocationData> processGeolocationData(String response) throws JsonProcessingException;
    String fetchReverseGeolocationData(String lat, String lon);
    String processReverseGeolocationData(String response) throws JsonProcessingException;
}
