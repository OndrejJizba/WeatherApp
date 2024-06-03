package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.repositories.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WeatherRepository weatherRepository;

    @BeforeEach
    void setUp() {
        weatherRepository.deleteAll();
    }

    @Test
    void getCurrentWeatherSuccessful() throws Exception {
        int initialWeatherRepoSize = weatherRepository.findAll().size();

        mockMvc.perform(MockMvcRequestBuilders.get("/weather")
                        .param("lat", "44.34")
                        .param("lon", "10.99"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name", is("Zocca")))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.temperature", notNullValue()))
                .andExpect(jsonPath("$.sunrise", notNullValue()))
                .andExpect(jsonPath("$.sunset", notNullValue()));

        assertEquals(initialWeatherRepoSize + 1, weatherRepository.findAll().size());
    }

    @Test
    void getCurrentWeatherCityNotFoundException() throws Exception {
        int initialWeatherRepoSize = weatherRepository.findAll().size();

        mockMvc.perform(MockMvcRequestBuilders.get("/weather")
                        .param("lat", "200.00")
                        .param("lon", "200.00"))
                .andExpect(status().is(404))
                .andExpect(content().string("City not found for given coordinates."));

        assertEquals(initialWeatherRepoSize, weatherRepository.findAll().size());
    }

    @Test
    void searchByNameSuccessfulOneResult() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("name", "Brno"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("Brno")))
                .andExpect(jsonPath("$[0].country", is("CZ")))
                .andExpect(jsonPath("$[0].lat", notNullValue()))
                .andExpect(jsonPath("$[0].lon", notNullValue()));
    }

    @Test
    void searchByNameSuccessfulMoreResults() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                        .param("name", "Prague"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[0].name", is("Prague")))
                .andExpect(jsonPath("$[4].name", is("Prague")))
                .andExpect(jsonPath("$[1].lat", notNullValue()))
                .andExpect(jsonPath("$[2].lon", notNullValue()))
                .andExpect(jsonPath("$[3].country", notNullValue()));
    }

    @Test
    void searchByNameCityNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/search")
                .param("name", "qwertzuiop"))
                .andExpect(status().is(404))
                .andExpect(content().string("City with given name doesn't exist."));
    }
}