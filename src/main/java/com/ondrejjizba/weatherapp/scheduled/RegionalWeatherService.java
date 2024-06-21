package com.ondrejjizba.weatherapp.scheduled;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ondrejjizba.weatherapp.models.*;
import com.ondrejjizba.weatherapp.models.DTOs.WeatherData;
import com.ondrejjizba.weatherapp.repositories.*;
import com.ondrejjizba.weatherapp.services.WeatherService;
import com.ondrejjizba.weatherapp.utils.UnixTimeConverter;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Component
public class RegionalWeatherService {
    private static final Logger logger = LoggerFactory.getLogger(RegionalWeatherService.class);
    private final RegionalCityRepository regionalCityRepository;
    private final RegionalCityWeatherRepository regionalCityWeatherRepository;
    private final RegionalCityForecastRepository regionalCityForecastRepository;
    private final ForecastRepository forecastRepository;
    private final WeatherService weatherService;
    private final WeatherRepository weatherRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RegionalWeatherService(RegionalCityRepository regionalCitiesRepository, RegionalCityWeatherRepository regionalCityWeatherRepository, RegionalCityForecastRepository regionalCityForecastRepository, ForecastRepository forecastRepository, WeatherService weatherService, WeatherRepository weatherRepository) {
        this.regionalCityRepository = regionalCitiesRepository;
        this.regionalCityWeatherRepository = regionalCityWeatherRepository;
        this.regionalCityForecastRepository = regionalCityForecastRepository;
        this.forecastRepository = forecastRepository;
        this.weatherService = weatherService;
        this.weatherRepository = weatherRepository;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyRegionalCitiesWeatherUpdate() {
        logger.info("Executing hourly regional cities weather update");
        List<RegionalCity> cities = regionalCityRepository.findAll();
        for (RegionalCity city : cities) {
            try {
                logger.info("Updating weather for city: {}", city.getCity());
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
                logger.info("Weather update completed for city: {}", city.getCity());
            } catch (Exception e) {
                logger.error("Error updating weather for city: {}", city.getCity(), e);
            }
        }
    }

    private void setPicturePath(RegionalCity city) {
        Long cityId = city.getId();
        String picturePath = "/images/" + cityId + ".jpg";
        city.setPicture(picturePath);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyRegionalCitiesForecastUpdate() {
        logger.info("Executing daily regional cities forecast update");
        regionalCityForecastRepository.deleteAll();
        List<RegionalCity> cities = regionalCityRepository.findAll();
        for (RegionalCity city : cities) {
            try {
                logger.info("Updating forecast for city: {}", city.getCity());
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
                logger.info("Forecast update completed for city: {}", city.getCity());
            } catch (Exception e) {
                logger.error("Error updating forecast for city: {}", city.getCity(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyWeatherEntityUpdate() {
        logger.info("Executing hourly weather entity update");
        List<WeatherEntity> weatherEntities = weatherRepository.findAll();
        for (WeatherEntity weatherEntity : weatherEntities) {
            try {
                logger.info("Updating weather entity for city: {}", weatherEntity.getName());
                String response = weatherService.fetchWeatherData(String.valueOf(weatherEntity.getLat()), String.valueOf(weatherEntity.getLon()));
                WeatherData weatherData = objectMapper.readValue(response, WeatherData.class);
                weatherEntity.setTemperature(weatherData.getMain().getTemp());
                weatherEntity.setDescription(weatherData.getWeather()[0].getDescription());
                weatherEntity.setSunrise(UnixTimeConverter.converterTime(weatherData.getSys().getSunrise(), weatherData.getTimezone()));
                weatherEntity.setSunset(UnixTimeConverter.converterTime(weatherData.getSys().getSunset(), weatherData.getTimezone()));
                String icon = weatherData.getWeather()[0].getIcon();
                weatherEntity.setIcon("https://openweathermap.org/img/wn/" + icon + ".png");
                weatherRepository.save(weatherEntity);
                logger.info("Weather entity update completed for city: {}", weatherEntity.getName());
            } catch (Exception e) {
                logger.error("Error updating weather entity for city: {}", weatherEntity.getName(), e);
            }
        }
    }
}
