import React, { useState, useEffect } from "react";
import axios from "axios";
import "./WeatherList.css";

const WeatherList = () => {
  const [weatherData, setWeatherData] = useState([]);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState("");

  useEffect(() => {
    const fetchWeatherData = async () => {
      try {
        const response = await axios.get("/cities");
        setWeatherData(response.data);
        if (response.data.length > 0) {
          setLastUpdated(formatDate(response.data[0].updatedAt));
        }
      } catch (err) {
        setError(err.response?.data?.error || "Error fetching weather data");
      }
    };

    fetchWeatherData();
  }, []);

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, "0");
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, "0");
    const minutes = date.getMinutes().toString().padStart(2, "0");

    return `${day}-${month}-${year} ${hours}:${minutes}`;
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!weatherData.length) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="container">
      {weatherData.map((cityWeather, index) => (
        <div className="card" key={index}>
          <div
            className="card-image"
            style={{
              backgroundImage: `url(http://localhost:8080${cityWeather.picture})`
            }}
          ></div>
          <div className="card-content">
            <h1>{cityWeather.city}</h1>
            <p>{cityWeather.temperature}°C</p>
            <p>{cityWeather.description}</p>
            <p>Sunrise: {cityWeather.sunrise}</p>
            <p>Sunset: {cityWeather.sunset}</p>
          </div>
        </div>
      ))}
      <div className="last-updated">Last updated: {lastUpdated}</div>
    </div>
  );
};

export default WeatherList;
