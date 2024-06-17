package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.services.FavoriteCityService;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
public class FavoriteCityController {
    private final FavoriteCityService favoriteCityService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public FavoriteCityController(FavoriteCityService favoriteCityService,
                                  UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.favoriteCityService = favoriteCityService;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> listFavoriteCitiesByUser(@RequestHeader("Authorization") String jwtToken) {
        String token = jwtToken.substring(7);
        List<FavoriteCity> favoriteCities = favoriteCityService.listFavoriteCitiesByUser(token);
        return ResponseEntity.status(200).body(favoriteCities);
    }

    @PostMapping("/favorites")
    public ResponseEntity<?> addToFavorites(@RequestHeader("Authorization") String jwtToken, @RequestParam double lat, @RequestParam double lon) {
        String token = jwtToken.substring(7);
        String username = jwtTokenUtil.extractUsername(token);
        UserInfo user = userRepository.findByUsername(username);
        boolean existsByUserAndLatAndLon = user.getFavoriteCities().stream().anyMatch(u -> u.getLat() == lat && u.getLon() == lon);
        HashMap<String, String> result = new HashMap<>();
        if (!existsByUserAndLatAndLon) {
            favoriteCityService.addToFavorites(token, lat, lon);
            result.put("message", "The city was successfully added to your favorite list.");
            return ResponseEntity.status(200).body(result);
        } else {
            result.put("message", "The city is already in your favorite list.");
            return ResponseEntity.status(400).body(result);
        }
    }
}