package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegionalCitiesWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double temperature;
    private String description;
    private String sunrise;
    private String sunset;
    @OneToOne (fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RegionalCities regionalCities;
}
