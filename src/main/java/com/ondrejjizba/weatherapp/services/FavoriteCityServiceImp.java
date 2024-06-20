package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.FavoriteCityResponse;
import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
import com.ondrejjizba.weatherapp.repositories.FavoriteCityRepository;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.repositories.WeatherRepository;
import com.ondrejjizba.weatherapp.utils.CoordinateUtil;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteCityServiceImp implements FavoriteCityService {
    private final UserRepository userRepository;
    private final FavoriteCityRepository favoriteCityRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final GeolocationService geolocationService;
    private final WeatherRepository weatherRepository;
    private final WeatherService weatherService;

    @Autowired
    public FavoriteCityServiceImp(UserRepository userRepository, FavoriteCityRepository favoriteCityRepository, JwtTokenUtil jwtTokenUtil, GeolocationService geolocationService, WeatherRepository weatherRepository, WeatherService weatherService) {
        this.userRepository = userRepository;
        this.favoriteCityRepository = favoriteCityRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.geolocationService = geolocationService;
        this.weatherRepository = weatherRepository;
        this.weatherService = weatherService;
    }

    @Override
    public void addToFavorites(String jwtToken, double lat, double lon) throws JsonProcessingException {
        String username = jwtTokenUtil.extractUsername(jwtToken);
        UserInfo user = userRepository.findByUsername(username);
        FavoriteCity favoriteCity;
        if (favoriteCityRepository.existsByLatAndLon(lat, lon)) {
            favoriteCity = favoriteCityRepository.findByLatAndLon(lat, lon);
        } else {
            favoriteCity = new FavoriteCity(lat, lon);
            String fetchCityName = geolocationService.fetchReverseGeolocationData(String.valueOf(lat), String.valueOf(lon));
            String cityName = geolocationService.processReverseGeolocationData(fetchCityName);
            favoriteCity.setName(cityName);
            favoriteCityRepository.save(favoriteCity);
        }
        user.getFavoriteCities().add(favoriteCity);
        userRepository.save(user);
    }

    @Override
    public List<FavoriteCity> listFavoriteCitiesByUser(String jwtToken) {
        String username = jwtTokenUtil.extractUsername(jwtToken);
        UserInfo user = userRepository.findByUsername(username);
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(user);
        return favoriteCityRepository.findAllByUsers(userInfos);
    }

    @Override
    public boolean existsByLatAndLon(double lat, double lon) {
        return favoriteCityRepository.existsByLatAndLon(lat, lon);
    }

    @Override
    public List<FavoriteCityResponse> getFavoriteCityResponses(String jwtToken) throws IOException {
        List<FavoriteCity> favoriteCities = listFavoriteCitiesByUser(jwtToken);
        List<FavoriteCityResponse> response = new ArrayList<>();
        for (FavoriteCity favoriteCity : favoriteCities) {
            FavoriteCityResponse favCityResp = new FavoriteCityResponse();
            favCityResp.setName(favoriteCity.getName());
            favCityResp.setLat(favoriteCity.getLat());
            favCityResp.setLon(favoriteCity.getLon());
            List<WeatherEntity> weatherEntities = weatherRepository.findAll();
            WeatherEntity matchingWeatherEntity = null;
            for (WeatherEntity weatherEntity : weatherEntities) {
                if (CoordinateUtil.compareCoordinates(weatherEntity, favoriteCity)) {
                    matchingWeatherEntity = weatherEntity;
                    break;
                }
            }
            if (matchingWeatherEntity != null) {
                favCityResp.setTemp(matchingWeatherEntity.getTemperature());
                favCityResp.setIcon(matchingWeatherEntity.getIcon());
            } else {
                String fetchData = weatherService.fetchWeatherData(String.valueOf(favoriteCity.getLat()), String.valueOf(favoriteCity.getLon()));
                WeatherEntity weatherEntity = weatherService.processWeatherData(fetchData);
                favCityResp.setTemp(weatherEntity.getTemperature());
                favCityResp.setIcon(weatherEntity.getIcon());
            }
            response.add(favCityResp);
        }
        return response;
    }
}
