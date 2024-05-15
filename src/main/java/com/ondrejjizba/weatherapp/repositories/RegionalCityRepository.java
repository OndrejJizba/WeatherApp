package com.ondrejjizba.weatherapp.repositories;

import com.ondrejjizba.weatherapp.models.RegionalCity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionalCityRepository extends JpaRepository<RegionalCity, Long> {
}
