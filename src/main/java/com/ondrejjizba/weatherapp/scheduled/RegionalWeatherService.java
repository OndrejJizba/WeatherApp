package com.ondrejjizba.weatherapp.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.RegionalCities;
import com.ondrejjizba.weatherapp.models.RegionalCitiesWeather;
import com.ondrejjizba.weatherapp.repositories.RegionalCitiesRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCitiesWeatherRepository;
import com.ondrejjizba.weatherapp.services.WeatherService;
import com.ondrejjizba.weatherapp.utils.UnixTimeConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Component
public class RegionalWeatherService {
    private final RegionalCitiesRepository regionalCitiesRepository;
    private final RegionalCitiesWeatherRepository regionalCitiesWeatherRepository;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RegionalWeatherService(RegionalCitiesRepository regionalCitiesRepository, RegionalCitiesWeatherRepository regionalCitiesWeatherRepository, WeatherService weatherService) {
        this.regionalCitiesRepository = regionalCitiesRepository;
        this.regionalCitiesWeatherRepository = regionalCitiesWeatherRepository;
        this.weatherService = weatherService;
    }
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyRegionalCitiesWeatherUpdate() throws IOException {
        List<RegionalCities> cities = regionalCitiesRepository.findAll();
        for (RegionalCities city : cities) {
            String response = weatherService.fetchData(city.getLat(), city.getLon());
            WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);

            RegionalCitiesWeather regionalCitiesWeather = new RegionalCitiesWeather();
            if (city.getRegionalCitiesWeather() != null) {
                regionalCitiesWeather = regionalCitiesWeatherRepository.findById(city.getRegionalCitiesWeather().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find entity with ID " + city.getRegionalCitiesWeather().getId()));
            }

            regionalCitiesWeather.setTemperature(weatherData.getMain().getTemp());
            regionalCitiesWeather.setDescription(weatherData.getWeather()[0].getDescription());
            regionalCitiesWeather.setSunrise(UnixTimeConverter.converter(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
            regionalCitiesWeather.setSunset(UnixTimeConverter.converter(weatherData.getSys().getSunset(), weatherData.getTimezone()));
            regionalCitiesWeather.setUpdatedAt(LocalDateTime.now());
            city.setRegionalCitiesWeather(regionalCitiesWeather);
            regionalCitiesWeather.setRegionalCities(city);
            regionalCitiesWeatherRepository.save(regionalCitiesWeather);
            regionalCitiesRepository.save(city);
        }
    }
}
