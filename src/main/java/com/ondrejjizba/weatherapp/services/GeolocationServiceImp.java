package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.exceptions.CityNotFoundException;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocationServiceImp implements GeolocationService {
    private static final String API_KEY = System.getenv("API_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String fetchGeolocationData(String cityName) {
        String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
        HttpGet request = new HttpGet("http://api.openweathermap.org/geo/1.0/direct?q=" + encodedCityName + "&limit=5&appid=" + API_KEY);
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            return client.execute(request, new BasicHttpClientResponseHandler());
        } catch (IOException e) {
            throw new CityNotFoundException("City with given name doesn't exist.");
        }
    }

    @Override
    public List<GeolocationData> processGeolocationData(String response) throws JsonProcessingException {
        List<GeolocationData> geolocationData = objectMapper.readValue(response, new TypeReference<>() {});

        if (geolocationData.isEmpty()) {
            throw new CityNotFoundException("City with given name doesn't exist.");
        }

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
}
