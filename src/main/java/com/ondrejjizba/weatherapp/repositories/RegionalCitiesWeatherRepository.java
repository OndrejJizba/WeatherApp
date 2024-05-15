package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCitiesWeather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalCitiesWeatherRepository extends JpaRepository<RegionalCitiesWeather, Long> {
}
