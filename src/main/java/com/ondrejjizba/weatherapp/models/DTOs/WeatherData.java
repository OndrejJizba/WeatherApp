package com.ondrejjizba.weatherapp.models.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private Weather[] weather;
    private MainData main;
    private Sys sys;
    private String name;
    private Long timezone;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Weather {
        private String description;
        private String icon;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private double temp;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Sys {
        private Long sunrise;
        private Long sunset;
    }
}
