package com.ondrejjizba.weatherapp.services;

import com.ondrejjizba.weatherapp.configuration.SecurityConfig;
import com.ondrejjizba.weatherapp.models.DTOs.UsernamePasswordRequest;
import com.ondrejjizba.weatherapp.models.UserInfo;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, String> userRegistrationSuccessful(UsernamePasswordRequest request) {
        Map<String, String> result = new HashMap<>();

        UserInfo userInfo = new UserInfo(request.getUsername(),
                SecurityConfig.passwordEncoder().encode(request.getPassword()));
        userRepository.save(userInfo);

        result.put("username", userInfo.getUsername());
        result.put("id", String.valueOf(userInfo.getId()));
        return result;
    }
}
