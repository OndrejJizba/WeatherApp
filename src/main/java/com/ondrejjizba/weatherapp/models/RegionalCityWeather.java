package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegionalCityWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double temperature;
    private String description;
    private String sunrise;
    private String sunset;
    private String icon;
    @DateTimeFormat(pattern = "dd-MM-yy HH:mm:ss")
    private LocalDateTime updatedAt;
    @OneToOne (fetch = FetchType.LAZY)
    private RegionalCity regionalCity;

    @PrePersist
    protected void onCreate(){
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}
