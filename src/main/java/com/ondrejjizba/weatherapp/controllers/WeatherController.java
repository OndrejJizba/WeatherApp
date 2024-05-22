package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import com.ondrejjizba.weatherapp.models.RegionalCity;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
import com.ondrejjizba.weatherapp.repositories.RegionalCityRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCityWeatherRepository;
import com.ondrejjizba.weatherapp.scheduled.RegionalWeatherService;
import com.ondrejjizba.weatherapp.services.WeatherService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class WeatherController {
    private final WeatherService weatherService;
    private final RegionalWeatherService regionalWeatherService;
    private final RegionalCityRepository regionalCityRepository;
    private final RegionalCityWeatherRepository regionalCityWeatherRepository;

    @Autowired
    public WeatherController(WeatherService weatherService, RegionalWeatherService regionalWeatherService,
                             RegionalCityRepository regionalCityRepository,
                             RegionalCityWeatherRepository regionalCityWeatherRepository) {
        this.weatherService = weatherService;
        this.regionalWeatherService = regionalWeatherService;
        this.regionalCityRepository = regionalCityRepository;
        this.regionalCityWeatherRepository = regionalCityWeatherRepository;
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getCurrentWeather(@RequestParam String lat, @RequestParam String lon) throws IOException {
        String response = weatherService.fetchWeatherData(lat, lon);
        WeatherEntity weatherEntity = weatherService.processWeatherData(response);
        Map<String, Object> result = new HashMap<>();
        result.put("name", weatherEntity.getName());
        result.put("description", weatherEntity.getDescription());
        result.put("temperature", weatherEntity.getTemperature());
        result.put("sunrise", weatherEntity.getSunrise());
        result.put("sunset", weatherEntity.getSunset());
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam String name) throws IOException {
        String response = weatherService.fetchGeolocationData(name);
        List<GeolocationData> geolocationData = weatherService.processGeolocationData(response);
        return ResponseEntity.status(200).body(geolocationData);
    }

    @GetMapping("/city/{id}")
    public ResponseEntity<?> listRegionalCitiesWeather(@PathVariable Long id) throws Exception {
        Optional<RegionalCity> regionalCityOptional = regionalCityRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if(regionalCityOptional.isEmpty()) {
            result.put("error", "City with given ID doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }

        RegionalCity regionalCity = regionalCityOptional.get();
        if (regionalCityWeatherRepository.findAll().isEmpty()) {
            regionalWeatherService.hourlyRegionalCitiesWeatherUpdate();
        }
        result.put("city", regionalCity.getCity());
        result.put("temperature", regionalCity.getRegionalCityWeather().getTemperature());
        result.put("description", regionalCity.getRegionalCityWeather().getDescription());
        result.put("sunrise", regionalCity.getRegionalCityWeather().getSunrise());
        result.put("sunset", regionalCity.getRegionalCityWeather().getSunset());
        result.put("updatedAt", regionalCity.getRegionalCityWeather().getUpdatedAt());
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("test")
    public ResponseEntity<?> test(@RequestParam String lat, @RequestParam String lon) throws IOException {
        String apiKey = System.getenv("API_KEY");
        HttpGet request = new HttpGet("https://api.openweathermap.org/data/2.5/weather?units=metric&lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
        CloseableHttpClient client = HttpClients.createDefault();
        String response = client.execute(request, new BasicHttpClientResponseHandler());
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/test2")
    public ResponseEntity<?> test2() throws IOException {
        regionalWeatherService.hourlyRegionalCitiesWeatherUpdate();
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/test3")
    public ResponseEntity<?> test3() throws IOException {
        String response = weatherService.fetchGeolocationData("London");
        weatherService.processGeolocationData(response);
        return ResponseEntity.status(200).build();
    }

    @GetMapping("/test4")
    public ResponseEntity<?> test4(@RequestParam String lat, @RequestParam String lon) throws IOException {
        String response = weatherService.fetchForecastData(lat, lon);
        return ResponseEntity.status(200).body(weatherService.processForecastData(response));
    }

    @GetMapping("test5")
    public ResponseEntity<?> test5() throws IOException {
        regionalWeatherService.dailyRegionalCitiesForecastUpdate();
        return ResponseEntity.status(200).build();
    }
}
