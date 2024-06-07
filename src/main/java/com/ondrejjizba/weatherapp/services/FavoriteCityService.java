package com.ondrejjizba.weatherapp.services;

import com.ondrejjizba.weatherapp.models.FavoriteCity;

import java.util.List;

public interface FavoriteCityService {
    void addToFavorites(String jwtToken, double lat, double lon);
    List<FavoriteCity> listFavoriteCitiesByUser(String jwtToken);
}
