package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.exceptions.CityNotFoundException;
import com.ondrejjizba.weatherapp.models.DTOs.GeolocationData;
import com.ondrejjizba.weatherapp.models.ForecastEntity;
import com.ondrejjizba.weatherapp.models.RegionalCity;
import com.ondrejjizba.weatherapp.models.RegionalCityForecast;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
import com.ondrejjizba.weatherapp.repositories.RegionalCityForecastRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCityRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCityWeatherRepository;
import com.ondrejjizba.weatherapp.scheduled.RegionalWeatherService;
import com.ondrejjizba.weatherapp.services.GeolocationService;
import com.ondrejjizba.weatherapp.services.WeatherService;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
public class WeatherController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    private final WeatherService weatherService;
    private final RegionalWeatherService regionalWeatherService;
    private final RegionalCityRepository regionalCityRepository;
    private final RegionalCityWeatherRepository regionalCityWeatherRepository;
    private final RegionalCityForecastRepository regionalCityForecastRepository;
    private final GeolocationService geolocationService;

    @Autowired
    public WeatherController(WeatherService weatherService, RegionalWeatherService regionalWeatherService,
                             RegionalCityRepository regionalCityRepository,
                             RegionalCityWeatherRepository regionalCityWeatherRepository,
                             RegionalCityForecastRepository regionalCityForecastRepository, GeolocationService geolocationService) {
        this.weatherService = weatherService;
        this.regionalWeatherService = regionalWeatherService;
        this.regionalCityRepository = regionalCityRepository;
        this.regionalCityWeatherRepository = regionalCityWeatherRepository;
        this.regionalCityForecastRepository = regionalCityForecastRepository;
        this.geolocationService = geolocationService;
    }

    @GetMapping("/weather")
    public ResponseEntity<?> getCurrentWeather(@RequestParam String lat, @RequestParam String lon) {
        logger.info("Received request for current weather with lat: {} and lon: {}", lat, lon);
        try {
            String response = weatherService.fetchWeatherData(lat, lon);
            WeatherEntity weatherEntity = weatherService.processWeatherData(response);
            Map<String, Object> result = new HashMap<>();
            result.put("name", weatherEntity.getName());
            result.put("description", weatherEntity.getDescription());
            result.put("temperature", weatherEntity.getTemperature());
            result.put("sunrise", weatherEntity.getSunrise());
            result.put("sunset", weatherEntity.getSunset());
            result.put("icon", weatherEntity.getIcon());
            logger.info("Successfully fetched current weather for lat: {} and lon: {}", lat, lon);
            return ResponseEntity.status(200).body(result);
        } catch (CityNotFoundException e) {
            logger.info("City not found for lat: {} and lon: {}", lat, lon, e);
            return ResponseEntity.status(404).body("City not found for given coordinates.");
        } catch (IOException e) {
            logger.error("Error fetching weather data for lat: {} and lon: {}", lat, lon, e);
            return ResponseEntity.status(500).body("Error fetching weather data.");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByName(@RequestParam String name) {
        logger.info("Received request to search city by name: {}", name);
        try {
            String response = geolocationService.fetchGeolocationData(name);
            List<GeolocationData> geolocationData = geolocationService.processGeolocationData(response);
            logger.info("Successfully fetched geolocation data for city: {}", name);
            return ResponseEntity.status(200).body(geolocationData);
        } catch (CityNotFoundException e) {
            logger.info("City not found with name: {}", name, e);
            return ResponseEntity.status(404).body("City with given name doesn't exist.");
        } catch (IOException e) {
            logger.error("Error fetching geolocation data for city: {}", name, e);
            return ResponseEntity.status(500).body("Error fetching geolocation data.");
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<?> listAllCitiesWeather() {
        logger.info("Received request for weather of all regional cities.");
        if (regionalCityWeatherRepository.findAll().isEmpty()) {
            logger.info("Weather data is empty, updating weather for all regional cities.");
            regionalWeatherService.hourlyRegionalCitiesWeatherUpdate();
        }

        List<RegionalCity> regionalCities = regionalCityRepository.findAll();

        List<Map<String, Object>> result = new ArrayList<>();
        for (RegionalCity regionalCity : regionalCities) {
            Map<String, Object> cityWeather = new HashMap<>();
            cityWeather.put("city", regionalCity.getCity());
            cityWeather.put("temperature", regionalCity.getRegionalCityWeather().getTemperature());
            cityWeather.put("description", regionalCity.getRegionalCityWeather().getDescription());
            cityWeather.put("sunrise", regionalCity.getRegionalCityWeather().getSunrise());
            cityWeather.put("sunset", regionalCity.getRegionalCityWeather().getSunset());
            cityWeather.put("picture", regionalCity.getPicture());
            cityWeather.put("updatedAt", regionalCity.getRegionalCityWeather().getUpdatedAt());
            cityWeather.put("id", regionalCity.getId());
            cityWeather.put("icon", regionalCity.getRegionalCityWeather().getIcon());
            cityWeather.put("lat", regionalCity.getLat());
            cityWeather.put("lon", regionalCity.getLon());
            result.add(cityWeather);
        }
        logger.info("Successfully received weather for all regional cities.");
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/forecast/{id}")
    public ResponseEntity<?> getForecastByCity(@PathVariable Long id) {
        logger.info("Received request for forecast of regional city with ID: {}", id);
        Optional<RegionalCity> regionalCityOptional = regionalCityRepository.findById(id);
        Map<String, Object> result = new HashMap<>();
        if (regionalCityOptional.isEmpty()) {
            logger.info("City with ID: {} doesn't exist", id);
            result.put("error", "City with given ID doesn't exist.");
            return ResponseEntity.status(400).body(result);
        }

        RegionalCity regionalCity = regionalCityOptional.get();
        if (regionalCityForecastRepository.findAll().isEmpty()) {
            logger.info("Forecast data is empty, updating forecast for regional cities.");
            regionalWeatherService.dailyRegionalCitiesForecastUpdate();
        }

        List<RegionalCityForecast> forecast = regionalCityForecastRepository.findAllByRegionalCityId(id);

        result.put("city", regionalCity.getCity());
        result.put("forecast", forecast);
        logger.info("Successfully received forecast for regional city with ID: {}", id);
        return ResponseEntity.status(200).body(result);
    }

    @GetMapping("/forecast")
    public ResponseEntity<?> getForecast(@RequestParam String lat, @RequestParam String lon) throws IOException {
        logger.info("Received request for forecast with lat: {} and lon: {}", lat, lon);
        try {
            String response = weatherService.fetchForecastData(lat, lon);
            List<ForecastEntity> forecast = weatherService.processForecastDataNoSave(response);
            logger.info("Successfully fetched forecast for lat: {} and lon: {}", lat, lon);
            return ResponseEntity.status(200).body(forecast);
        } catch (IOException e) {
            logger.error("Error fetching forecast data for lat: {} and lon: {}", lat, lon, e);
            return ResponseEntity.status(500).body("Error fetching forecast data.");
        }
    }
}
