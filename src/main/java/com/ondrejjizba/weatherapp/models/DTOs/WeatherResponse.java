package com.ondrejjizba.weatherapp.models.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    private Weather[] weather;
    private MainData main;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private double temp;
    }
}
