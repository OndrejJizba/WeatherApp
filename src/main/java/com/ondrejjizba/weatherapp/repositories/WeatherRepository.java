package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {
    WeatherEntity findByName(String name);
    boolean existsByLatAndLon(double lat, double lon);
    WeatherEntity findByLatAndLon(double lat, double lon);
}
