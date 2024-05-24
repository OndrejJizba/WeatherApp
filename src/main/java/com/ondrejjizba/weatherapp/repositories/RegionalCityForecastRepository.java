package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCityForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionalCityForecastRepository extends JpaRepository<RegionalCityForecast, Long> {
    List<RegionalCityForecast> findAllByRegionalCityId (Long id);
}
