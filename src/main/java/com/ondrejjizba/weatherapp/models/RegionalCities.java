package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegionalCities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String city;
    private String lat;
    private String lon;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RegionalCitiesWeather regionalCitiesWeather;
}
