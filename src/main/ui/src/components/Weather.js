import React, { useState, useEffect } from "react";
import axios from "axios";

const Weather = ({ cityId }) => {
  const [weatherData, setWeatherData] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchWeatherData = async () => {
      try {
        const response = await axios.get(`/city/${cityId}`);
        setWeatherData(response.data);
      } catch (err) {
        setError(err.response?.data?.error || "Error fetching weather data");
      }
    };

    fetchWeatherData();
  }, [cityId]);

  if (error) {
    return <div>{error}</div>;
  }

  if (!weatherData) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h1>Weather in {weatherData.city}</h1>
      <p>Temperature: {weatherData.temperature}</p>
      <p>Description: {weatherData.description}</p>
      <p>Sunrise: {weatherData.sunrise}</p>
      <p>Sunset: {weatherData.sunset}</p>
      <p>Last updated: {weatherData.updatedAt}</p>
    </div>
  );
};

export default Weather;
