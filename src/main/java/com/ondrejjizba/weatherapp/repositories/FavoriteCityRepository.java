package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteCityRepository extends JpaRepository<FavoriteCity, Long> {
    boolean existsByLatAndLon (double lat, double lon);
    FavoriteCity findByLatAndLon (double lat, double lon);
    List<FavoriteCity> findAllByUsers(List<UserInfo> user);
}
