package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.exceptions.ValidationError;
import com.ondrejjizba.weatherapp.models.DTOs.RegistrationRequest;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService,
                          UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody RegistrationRequest request,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ValidationError.handleErrors(bindingResult);
        }
        Map<String, String> result = new HashMap<>();
        if (userRepository.existsByUsername(request.getUsername())) {
            result.put("error", "Username already exists.");
            return ResponseEntity.status(400).body(result);
        }
        return ResponseEntity.status(200).body(userService.userRegistrationSuccessful(request));
    }
}
