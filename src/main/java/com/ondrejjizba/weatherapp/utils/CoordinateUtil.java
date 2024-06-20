package com.ondrejjizba.weatherapp.utils;

import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.WeatherEntity;

public class CoordinateUtil {
    public static String formatCoordinate(double coordinate) {
        return String.format("%.2f", coordinate);
    }
    public static boolean compareCoordinates(WeatherEntity weatherEntity, FavoriteCity favoriteCity) {
        String formattedWeatherEntityLat, formattedWeatherEntityLon;
        if (weatherEntity != null) {
            formattedWeatherEntityLat = formatCoordinate(weatherEntity.getLat());
            formattedWeatherEntityLon = formatCoordinate(weatherEntity.getLon());
        } else {
            formattedWeatherEntityLat = "";
            formattedWeatherEntityLon = "";
        }
        String formattedFavoriteCityLat = formatCoordinate(favoriteCity.getLat());
        String formattedFavoriteCityLon = formatCoordinate(favoriteCity.getLon());
        return formattedWeatherEntityLat.equals(formattedFavoriteCityLat) && formattedWeatherEntityLon.equals(formattedFavoriteCityLon);
    }
}
