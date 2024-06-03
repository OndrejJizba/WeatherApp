package com.ondrejjizba.weatherapp.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.models.ForecastEntity;
import com.ondrejjizba.weatherapp.models.RegionalCity;
import com.ondrejjizba.weatherapp.models.RegionalCityForecast;
import com.ondrejjizba.weatherapp.models.RegionalCityWeather;
import com.ondrejjizba.weatherapp.repositories.ForecastRepository;
import com.ondrejjizba.weatherapp.repositories.RegionalCityForecastRepository;
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
    private final RegionalCityForecastRepository regionalCityForecastRepository;
    private final ForecastRepository forecastRepository;
    private final WeatherService weatherService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RegionalWeatherService(RegionalCityRepository regionalCitiesRepository, RegionalCityWeatherRepository regionalCityWeatherRepository, RegionalCityForecastRepository regionalCityForecastRepository, ForecastRepository forecastRepository, WeatherService weatherService) {
        this.regionalCityRepository = regionalCitiesRepository;
        this.regionalCityWeatherRepository = regionalCityWeatherRepository;
        this.regionalCityForecastRepository = regionalCityForecastRepository;
        this.forecastRepository = forecastRepository;
        this.weatherService = weatherService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyRegionalCitiesWeatherUpdate() throws IOException {
        List<RegionalCity> cities = regionalCityRepository.findAll();
        for (RegionalCity city : cities) {
            String response = weatherService.fetchWeatherData(city.getLat(), city.getLon());
            WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);

            RegionalCityWeather regionalCityWeather = new RegionalCityWeather();
            if (city.getRegionalCityWeather() != null) {
                regionalCityWeather = regionalCityWeatherRepository.findById(city.getRegionalCityWeather().getId())
                        .orElseThrow(() -> new EntityNotFoundException("Cannot find entity with ID " + city.getRegionalCityWeather().getId()));
            }

            if (city.getPicture() == null) setPicturePath(city);

            regionalCityWeather.setTemperature(weatherData.getMain().getTemp());
            regionalCityWeather.setDescription(weatherData.getWeather()[0].getDescription());
            regionalCityWeather.setSunrise(UnixTimeConverter.converterTime(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
            regionalCityWeather.setSunset(UnixTimeConverter.converterTime(weatherData.getSys().getSunset(), weatherData.getTimezone()));
            String icon = weatherData.getWeather()[0].getIcon();
            regionalCityWeather.setIcon("https://openweathermap.org/img/wn/" + icon + ".png");
            regionalCityWeather.setUpdatedAt(LocalDateTime.now());
            city.setRegionalCityWeather(regionalCityWeather);
            regionalCityWeather.setRegionalCity(city);
            regionalCityWeatherRepository.save(regionalCityWeather);
            regionalCityRepository.save(city);
        }
    }

    private void setPicturePath(RegionalCity city) {
        Long cityId = city.getId();
        String picturePath = "/images/" + cityId + ".jpg";
        city.setPicture(picturePath);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyRegionalCitiesForecastUpdate() throws IOException {
        regionalCityForecastRepository.deleteAll();
        List<RegionalCity> cities = regionalCityRepository.findAll();
        for (RegionalCity city : cities) {
            String response = weatherService.fetchForecastData(city.getLat(), city.getLon());
            List<ForecastEntity> forecasts = weatherService.processForecastData(response);
            for (ForecastEntity forecast : forecasts) {
                RegionalCityForecast regionalCityForecast = new RegionalCityForecast();
                regionalCityForecast.setTime(UnixTimeConverter.converterDayTime(forecast.getDt(), forecast.getTimezone()));
                regionalCityForecast.setDescription(forecast.getDescription());
                regionalCityForecast.setTemperature(forecast.getTemp());
                String icon = forecast.getIcon();
                regionalCityForecast.setIcon("https://openweathermap.org/img/wn/" + icon + "@2x.png");
                regionalCityForecast.setRegionalCity(city);
                regionalCityForecastRepository.save(regionalCityForecast);
            }
            forecastRepository.deleteAll();
        }
    }
}
