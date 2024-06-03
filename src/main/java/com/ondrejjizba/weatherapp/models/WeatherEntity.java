package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class WeatherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private double temperature;
    private String sunrise;
    private String sunset;
    private String icon;

    public WeatherEntity(String name, String description, double temperature, String sunrise, String sunset) {
        this.name = name;
        this.description = description;
        this.temperature = temperature;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }
}
