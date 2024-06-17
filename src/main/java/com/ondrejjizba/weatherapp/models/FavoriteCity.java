package com.ondrejjizba.weatherapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FavoriteCity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double lat;
    private double lon;
    private String name;
    @JsonIgnore
    @ManyToMany(mappedBy = "favoriteCities")
    List<UserInfo> users = new ArrayList<>();

    public FavoriteCity(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
