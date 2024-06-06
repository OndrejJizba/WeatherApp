package com.ondrejjizba.weatherapp.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
    private String password;
    @ManyToMany
    @JoinTable(name="user_city",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "city_id"))
    private List<FavoriteCity> favoriteCities;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
