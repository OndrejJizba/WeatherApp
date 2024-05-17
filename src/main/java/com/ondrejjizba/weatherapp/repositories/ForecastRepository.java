package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {
}
