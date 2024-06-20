package com.ondrejjizba.weatherapp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ondrejjizba.weatherapp.models.DTOs.FavoriteCityResponse;
import com.ondrejjizba.weatherapp.models.FavoriteCity;

import java.io.IOException;
import java.util.List;

public interface FavoriteCityService {
    void addToFavorites(String jwtToken, double lat, double lon) throws JsonProcessingException;
    List<FavoriteCity> listFavoriteCitiesByUser(String jwtToken);
    boolean existsByLatAndLon(double lat, double lon);
    List<FavoriteCityResponse> getFavoriteCityResponses(String jwtToken) throws IOException;
}
