package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegionalCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String lat;
    private String lon;
    @OneToOne(fetch = FetchType.LAZY)
    private RegionalCityWeather regionalCityWeather;
    @OneToMany (cascade = CascadeType.ALL, mappedBy = "regionalCity")
    private List<RegionalCityForecast> forecast;
}
