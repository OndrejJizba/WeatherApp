package com.ondrejjizba.weatherapp.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.RegionalCity;
import com.ondrejjizba.weatherapp.models.RegionalCityWeather;
import com.ondrejjizba.weatherapp.repositories.RegionalCityRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCityWeatherRepository;
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
    private final RegionalCityRepository regionalCityRepository;
    private final RegionalCityWeatherRepository regionalCityWeatherRepository;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RegionalWeatherService(RegionalCityRepository regionalCitiesRepository, RegionalCityWeatherRepository regionalCityWeatherRepository, WeatherService weatherService) {
        this.regionalCityRepository = regionalCitiesRepository;
        this.regionalCityWeatherRepository = regionalCityWeatherRepository;
        this.weatherService = weatherService;
    }
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyRegionalCitiesWeatherUpdate() throws IOException {
        List<RegionalCity> cities = regionalCityRepository.findAll();
        for (RegionalCity city : cities) {
            String response = weatherService.fetchData(city.getLat(), city.getLon());
            WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);

            RegionalCityWeather regionalCityWeather = new RegionalCityWeather();
            if (city.getRegionalCityWeather() != null) {
                regionalCityWeather = regionalCityWeatherRepository.findById(city.getRegionalCityWeather().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find entity with ID " + city.getRegionalCityWeather().getId()));
            }

            regionalCityWeather.setTemperature(weatherData.getMain().getTemp());
            regionalCityWeather.setDescription(weatherData.getWeather()[0].getDescription());
            regionalCityWeather.setSunrise(UnixTimeConverter.converter(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
            regionalCityWeather.setSunset(UnixTimeConverter.converter(weatherData.getSys().getSunset(), weatherData.getTimezone()));
            regionalCityWeather.setUpdatedAt(LocalDateTime.now());
            city.setRegionalCityWeather(regionalCityWeather);
            regionalCityWeather.setRegionalCity(city);
            regionalCityWeatherRepository.save(regionalCityWeather);
            regionalCityRepository.save(city);
        }
    }
}
