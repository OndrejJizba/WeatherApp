package com.ondrejjizba.weatherapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.FavoriteCityResponse;
import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.models.WeatherEntity;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.services.FavoriteCityService;
import com.ondrejjizba.weatherapp.services.WeatherService;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class FavoriteCityController {
    private static final Logger logger = LoggerFactory.getLogger(FavoriteCityController.class);
    private final FavoriteCityService favoriteCityService;
    private final WeatherService weatherService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public FavoriteCityController(FavoriteCityService favoriteCityService, WeatherService weatherService,
                                  UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.favoriteCityService = favoriteCityService;
        this.weatherService = weatherService;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> listFavoriteCitiesByUser(@RequestHeader("Authorization") String jwtToken) throws IOException {
        logger.info("Received request to list favorite cities by user " + jwtTokenUtil.extractUsername(jwtToken.substring(7)));
        String token = jwtToken.substring(7);
        List<FavoriteCity> favoriteCities = favoriteCityService.listFavoriteCitiesByUser(token);
        List<FavoriteCityResponse> response = new ArrayList<>();
        for (FavoriteCity favoriteCity : favoriteCities) {
            FavoriteCityResponse favCityResp = new FavoriteCityResponse();
            favCityResp.setName(favoriteCity.getName());
            favCityResp.setLat(favoriteCity.getLat());
            favCityResp.setLon(favoriteCity.getLon());
            String fetchData = weatherService.fetchWeatherData(String.valueOf(favoriteCity.getLat()), String.valueOf(favoriteCity.getLon()));
            WeatherEntity weatherEntity = weatherService.processWeatherData(fetchData);
            favCityResp.setTemp(weatherEntity.getTemperature());
            favCityResp.setIcon(weatherEntity.getIcon());
            response.add(favCityResp);
        }
        logger.info("Listing favorite cities by user " + jwtTokenUtil.extractUsername(jwtToken.substring(7)) + " successful.");
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addToFavorites(@RequestHeader("Authorization") String jwtToken, @RequestParam double lat, @RequestParam double lon) throws JsonProcessingException {
        logger.info("Received request to add city to favorites, lat: {}, lon: {}", lat, lon);
        String token = jwtToken.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        UserInfo user = userRepository.findByUsername(username);
        boolean existsByUserAndLatAndLon = user.getFavoriteCities().stream().anyMatch(u -> u.getLat() == lat && u.getLon() == lon);
        HashMap<String, String> result = new HashMap<>();
        if (!existsByUserAndLatAndLon) {
            favoriteCityService.addToFavorites(token, lat, lon);
            result.put("message", "The city was successfully added to your favorite list.");
            logger.info("Adding city to favorites, lat: {}, lon: {}", lat, lon);
            return ResponseEntity.status(200).body(result);
        } else {
            result.put("message", "The city is already in your favorite list.");
            logger.info("City is already in favorites, lat: {}, lon: {}", lat, lon);
            return ResponseEntity.status(400).body(result);
        }
    }
}
