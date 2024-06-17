package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.repositories.FavoriteCityRepository;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteCityServiceImp implements FavoriteCityService {
    private final UserRepository userRepository;
    private final FavoriteCityRepository favoriteCityRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final GeolocationService geolocationService;

    @Autowired
    public FavoriteCityServiceImp(UserRepository userRepository, FavoriteCityRepository favoriteCityRepository, JwtTokenUtil jwtTokenUtil, GeolocationService geolocationService) {
        this.userRepository = userRepository;
        this.favoriteCityRepository = favoriteCityRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.geolocationService = geolocationService;
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
}
