package com.ondrejjizba.weatherapp.services;

import com.ondrejjizba.weatherapp.models.DTOs.RegistrationRequest;

import java.util.Map;

public interface UserService {
    Map<String, String> userRegistrationSuccessful(RegistrationRequest request);
}
