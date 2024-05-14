package com.ondrejjizba.weatherapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherResponse;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WeatherController {

    @GetMapping("/weather")
    public ResponseEntity<?> getCurrentWeather(@RequestParam String lat, @RequestParam String lon) throws IOException {
        String apiKey = "5ca9d598d2838310f457258b0c48720c";
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
        CloseableHttpClient client = HttpClients.createDefault();
        String response = client.execute(request, new BasicHttpClientResponseHandler());
        ObjectMapper objectMapper = new ObjectMapper();
        WeatherResponse weatherResponse = objectMapper.readValue(response, WeatherResponse.class);
        String weatherDescription = weatherResponse.getWeather()[0].getDescription();
        double temperature = weatherResponse.getMain().getTemp();
        Map<String, Object> result = new HashMap<>();
        result.put("description", weatherDescription);
        result.put("temperature", temperature);
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("test")
    public ResponseEntity<?> test(@RequestParam String lat, @RequestParam String lon) throws IOException {
        String apiKey = "5ca9d598d2838310f457258b0c48720c";
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
        CloseableHttpClient client = HttpClients.createDefault();
        String response = client.execute(request, new BasicHttpClientResponseHandler());
        return ResponseEntity.status(200).body(response);
    }
}
