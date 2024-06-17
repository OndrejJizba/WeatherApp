package com.ondrejjizba.weatherapp.controllers;

import com.ondrejjizba.weatherapp.repositories.RegionalCityRepository;
import com.ondrejjizba.weatherapp.repositories.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WeatherRepository weatherRepository;
    @Autowired
    private RegionalCityRepository regionalCityRepository;

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

    @Test
    void listAllCitiesWeatherTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cities"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(equalTo(13))))
                .andExpect(jsonPath("$[0].city", is(notNullValue())))
                .andExpect(jsonPath("$[0].temperature", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(notNullValue())))
                .andExpect(jsonPath("$[0].sunrise", is(notNullValue())))
                .andExpect(jsonPath("$[0].sunset", is(notNullValue())))
                .andExpect(jsonPath("$[0].picture", is(notNullValue())))
                .andExpect(jsonPath("$[0].updatedAt", is(notNullValue())))
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].icon", is(notNullValue())))
                .andExpect(jsonPath("$[0].lat", is(notNullValue())))
                .andExpect(jsonPath("$[0].lon", is(notNullValue())));
    }

    @Test
    void getForecastByCitySuccessful() throws Exception {
        String cityName = regionalCityRepository.findById(1L).get().getCity();
        mockMvc.perform(MockMvcRequestBuilders.get("/forecast/1"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.city", is(cityName)))
                .andExpect(jsonPath("$.forecast", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.forecast[0].time", is(notNullValue())))
                .andExpect(jsonPath("$.forecast[0].temperature", is(notNullValue())))
                .andExpect(jsonPath("$.forecast[0].description", is(notNullValue())))
                .andExpect(jsonPath("$.forecast[0].icon", is(notNullValue())));
    }

    @Test
    void getForecastByCityCityNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/forecast/100"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.error", is("City with given ID doesn't exist.")));
    }

    @Test
    void getForecastSuccessful() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/forecast")
                        .param("lat", "44.34")
                        .param("lon", "10.99"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.[0].dt", is(notNullValue())))
                .andExpect(jsonPath("$.[0].temp", is(notNullValue())))
                .andExpect(jsonPath("$.[0].description", is(notNullValue())))
                .andExpect(jsonPath("$.[0].timezone", is(notNullValue())))
                .andExpect(jsonPath("$.[0].icon", is(notNullValue())));
    }

    @Test
    void getForecastCityNotFoundException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/forecast")
                        .param("lat", "200.00")
                        .param("lon", "200.00"))
                .andExpect(status().is(500))
                .andExpect(content().string("Error fetching forecast data."));
    }
}