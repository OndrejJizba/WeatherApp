import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "./WeatherList.css";
import Modal from "./Modal";

const WeatherList = () => {
  const [weatherData, setWeatherData] = useState([]);
  const [error, setError] = useState(null);
  const [lastUpdated, setLastUpdated] = useState("");
  const [modalMessage, setModalMessage] = useState("");

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

  const addToFavorites = async (lat, lon) => {
    const token = localStorage.getItem("token");
    if (!token) {
      setModalMessage("Please login to add favorites.");
      return;
    }

    try {
      const params = new URLSearchParams({ lat, lon });
      const response = await axios.post(`/favorites?${params.toString()}`, null, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setModalMessage(response.data.message);
    } catch (err) {
      setModalMessage(err.response?.data?.message || "Error adding to favorites");
    }
  };

  const closeModal = () => {
    setModalMessage("");
  };

  if (error) {
    return <div className="error">{error}</div>;
  }

  if (!weatherData.length) {
    return <div className="loading">Loading...</div>;
  }

  function isUserLoggedIn() {
    const token = localStorage.getItem('token');
    return !!token;
  }

  return (
    <div className="container">
      {weatherData.map((cityWeather) => (
        <div key={cityWeather.id} className="card-link">
          <Link to={`/weather/${cityWeather.id}`} className="card-link">
            <div className="card">
              <div
                className="card-image"
                style={{
                  backgroundImage: `url(http://localhost:8080${cityWeather.picture})`,
                }}
              ></div>
              <div className="card-content">
                <h1>
                  {cityWeather.city}
                  {isUserLoggedIn() && (
                    <button
                    className="add-favorite-button"
                    onClick={(e) => {
                      e.preventDefault();
                      addToFavorites(cityWeather.lat, cityWeather.lon);
                    }}
                  >
                    <img
                    src="/icons/like.png"
                    alt="favorite icon"
                    className="icon"
                  />
                  </button>
                  )}
                </h1>
                <h5>{Math.round(cityWeather.temperature)}°C</h5>
                <p>
                  <img
                    src={cityWeather.icon}
                    alt="Weather Icon"
                    className="weather-icon"
                  />
                </p>
                <p>
                  <img
                    src="/icons/sunrise.png"
                    alt="Sunrise"
                    className="icon"
                  />{" "}
                  {cityWeather.sunrise}
                </p>
                <p>
                  <img
                    src="/icons/sunset.png"
                    alt="Sunset"
                    className="icon"
                  />{" "}
                  {cityWeather.sunset}
                </p>
              </div>
            </div>
          </Link>
        </div>
      ))}
      <div className="last-updated">Last updated: {lastUpdated}</div>
      {modalMessage && <Modal message={modalMessage} onClose={closeModal} />}
    </div>
  );
};

export default WeatherList;
