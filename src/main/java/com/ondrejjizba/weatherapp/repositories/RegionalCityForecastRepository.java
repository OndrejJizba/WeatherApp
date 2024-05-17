package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCityForecast;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalCityForecastRepository extends JpaRepository<RegionalCityForecast, Long> {
}
