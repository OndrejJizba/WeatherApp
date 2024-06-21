package com.ondrejjizba.weatherapp.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteCityResponse {
    private String name;
    private double lat;
    private double lon;
    private double temp;
    private String icon;
    private Long id;
}
