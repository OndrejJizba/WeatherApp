import React, { useState, useEffect } from "react";
import axios from "axios";

const WeatherList = () => {
  const [weatherData, setWeatherData] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchWeatherData = async () => {
      try {
        const response = await axios.get("/cities");
        setWeatherData(response.data);
      } catch (err) {
        setError(err.response?.data?.error || "Error fetching weather data");
      }
    };

    fetchWeatherData();
  }, []);

  if (error) {
    return <div>{error}</div>;
  }

  if (!weatherData.length) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      {weatherData.map((cityWeather, index) => (
        <div key={index}>
          <h1>Weather in {cityWeather.city}</h1>
          <p>Temperature: {cityWeather.temperature}</p>
          <p>Description: {cityWeather.description}</p>
          <p>Sunrise: {cityWeather.sunrise}</p>
          <p>Sunset: {cityWeather.sunset}</p>
          <p>Last updated: {cityWeather.updatedAt}</p>
        </div>
      ))}
    </div>
  );
};

export default WeatherList;
