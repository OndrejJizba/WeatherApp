package com.ondrejjizba.weatherapp.models.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeolocationData {
    private String name;
    private double lat;
    private double lon;
    private String country;
}
