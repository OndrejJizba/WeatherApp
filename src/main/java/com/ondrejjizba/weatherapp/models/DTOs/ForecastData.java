package com.ondrejjizba.weatherapp.models.DTOs;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForecastData {
    private long dt;
    private double temp;
    private String description;
}
