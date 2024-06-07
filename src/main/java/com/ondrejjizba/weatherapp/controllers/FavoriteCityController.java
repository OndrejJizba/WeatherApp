package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.services.FavoriteCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoriteCityController {
    private final FavoriteCityService favoriteCityService;

    @Autowired
    public FavoriteCityController(FavoriteCityService favoriteCityService) {
        this.favoriteCityService = favoriteCityService;
    }

    @GetMapping("/favorites")
    public List<FavoriteCity> listFavoriteCitiesByUser(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.substring(7);
        return favoriteCityService.listFavoriteCitiesByUser(token);
    }

    @PostMapping("/favorites")
    public void addToFavorites(@RequestHeader("Authorization") String jwtToken, @RequestParam double lat, @RequestParam double lon) {
        String token = jwtToken.substring(7);
        favoriteCityService.addToFavorites(token, lat, lon);
    }
}
