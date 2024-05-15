package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCity;
import com.ondrejjizba.weatherapp.models.RegionalCityWeather;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalCityWeatherRepository extends JpaRepository<RegionalCityWeather, Long> {
    RegionalCity findByRegionalCity(RegionalCity regionalCity);
}
