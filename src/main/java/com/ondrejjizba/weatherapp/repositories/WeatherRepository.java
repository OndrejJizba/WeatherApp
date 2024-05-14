package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.WeatherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeatherRepository extends JpaRepository<WeatherEntity, Long> {
}
