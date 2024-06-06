package com.ondrejjizba.weatherapp.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

public class ValidationError {
    public static ResponseEntity<?> handleErrors(BindingResult bindingResult) {
        Map<String, String> result = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            result.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(400).body(result);
    }
}
