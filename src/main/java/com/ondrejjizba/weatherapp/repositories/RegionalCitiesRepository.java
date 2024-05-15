package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalCitiesRepository extends JpaRepository<RegionalCities, Long> {
}
