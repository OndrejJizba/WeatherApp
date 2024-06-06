package com.ondrejjizba.weatherapp.services;

import com.ondrejjizba.weatherapp.models.DTOs.UsernamePasswordRequest;

import java.util.Map;

public interface UserService {
    Map<String, String> userRegistrationSuccessful(UsernamePasswordRequest request);
}
