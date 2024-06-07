package com.ondrejjizba.weatherapp.services;

import com.ondrejjizba.weatherapp.models.FavoriteCity;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.repositories.FavoriteCityRepository;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FavoriteCityServiceImp implements FavoriteCityService{
    private final UserRepository userRepository;
    private final FavoriteCityRepository favoriteCityRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public FavoriteCityServiceImp(UserRepository userRepository, FavoriteCityRepository favoriteCityRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.favoriteCityRepository = favoriteCityRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void addToFavorites(String jwtToken, double lat, double lon) {
        String username = jwtTokenUtil.extractUsername(jwtToken);
        UserInfo user = userRepository.findByUsername(username);
        if (favoriteCityRepository.existsByLatAndLon(lat, lon)) {
            FavoriteCity favoriteCity = favoriteCityRepository.findByLatAndLon(lat, lon);
            user.getFavoriteCities().add(favoriteCity);
            userRepository.save(user);
        } else {
            FavoriteCity favoriteCity = new FavoriteCity(lat, lon);
            user.getFavoriteCities().add(favoriteCity);
            favoriteCityRepository.save(favoriteCity);
            userRepository.save(user);
        }
    }

    @Override
    public List<FavoriteCity> listFavoriteCitiesByUser(String jwtToken) {
        String username = jwtTokenUtil.extractUsername(jwtToken);
        UserInfo user = userRepository.findByUsername(username);
        List<UserInfo> userInfos = new ArrayList<>();
        userInfos.add(user);
        return favoriteCityRepository.findAllByUsers(userInfos);
    }
}
