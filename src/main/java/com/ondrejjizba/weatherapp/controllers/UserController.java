package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.exceptions.ValidationError;
import com.ondrejjizba.weatherapp.models.DTOs.UsernamePasswordRequest;
import com.ondrejjizba.weatherapp.repositories.UserRepository;
import com.ondrejjizba.weatherapp.services.UserDetailsServiceImp;
import com.ondrejjizba.weatherapp.services.UserService;
import com.ondrejjizba.weatherapp.utils.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    public UserController(UserService userService,
                          UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserDetailsServiceImp userDetailsServiceImp) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsServiceImp = userDetailsServiceImp;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody UsernamePasswordRequest request,
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

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody UsernamePasswordRequest request) throws Exception {
        authenticate(request.getUsername(), request.getPassword());

        UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(request.getUsername());
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> result = new HashMap<>();
        result.put("jwtToken", token);
        return ResponseEntity.status(200).body(result);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    @GetMapping("/test")
    public String test(){
        return "Hello";
    }
}
