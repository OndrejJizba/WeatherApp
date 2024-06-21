import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./SearchBar.css";
import Modal from "./Modal";

function SearchBar() {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [modalMessage, setModalMessage] = useState("");
  const navigate = useNavigate();

  const handleSearch = async () => {
    setSearchResults([]);
    setSelectedLocation(null);
    setError(null);
    setLoading(true);
    try {
      const response = await axios.get(`/search?name=${searchTerm}`);
      if (response.data.length > 0) {
        setSearchResults(response.data);
      } else {
        setError(`${searchTerm} doesn't exist.`);
      }
    } catch (error) {
      console.error("Error searching for geolocation data:", error);
      setError(`${searchTerm} doesn't exist.`);
    }
    setLoading(false);
  };

  const addToFavorites = async (lat, lon) => {
    const token = localStorage.getItem("token");
    if (!token) {
      setModalMessage("Please login to add favorites.");
      return;
    }

    try {
      const params = new URLSearchParams({ lat, lon });
      const response = await axios.post(
        `/favorites?${params.toString()}`,
        null,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setModalMessage(response.data.message);
    } catch (err) {
      setModalMessage(
        err.response?.data?.message || "Error adding to favorites"
      );
    }
  };

  const closeModal = () => {
    setModalMessage("");
  };

  const handleLocationSelect = async (location) => {
    setError(null);
    setLoading(true);
    try {
      const weatherResponse = await axios.get(
        `/weather?lat=${location.lat}&lon=${location.lon}`
      );
      setSelectedLocation({
        ...location,
        weather: weatherResponse.data,
      });
    } catch (error) {
      console.error("Error fetching current weather:", error);
      setError("Error fetching current weather. Please try again later.");
    }
    setLoading(false);
  };

  const handleForecastClick = () => {
    if (selectedLocation) {
      navigate(`/forecast/${selectedLocation.lat}/${selectedLocation.lon}`);
    }
  };

  function isUserLoggedIn() {
    const token = localStorage.getItem("token");
    return !!token;
  }

  return (
    <div className="search-bar-container">
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Enter city name..."
      />
      <button
        className="search-button"
        onClick={handleSearch}
        disabled={loading}
      >
        {loading ? <span className="loader"></span> : "Search"}
      </button>

      {error && <div className="error-message">{error}</div>}

      <ul>
        {searchResults.map((result) => (
          <li key={`${result.name}-${result.country}`}>
            <button
              onClick={() => handleLocationSelect(result)}
              disabled={loading}
            >
              {result.name}, {result.country}
            </button>
          </li>
        ))}
      </ul>

      {selectedLocation && selectedLocation.weather && (
        <div className="selected-location">
          <h2>
            {selectedLocation.name}, {selectedLocation.country}
          </h2>
          <h5>{Math.round(selectedLocation.weather.temperature)}°C</h5>
          <p>
            <img
              src={selectedLocation.weather.icon}
              alt="Weather Icon"
              className="weather-icon"
            />
          </p>
          <p>
            <img src="/icons/sunrise.png" alt="Sunrise" className="icon" />{" "}
            {selectedLocation.weather.sunrise}
          </p>
          <p>
            <img src="/icons/sunset.png" alt="Sunset" className="icon" />{" "}
            {selectedLocation.weather.sunset}
          </p>
          <button className="forecast-button" onClick={handleForecastClick}>
          <img src="/icons/forecast.png" alt="forecast icon" className="icon" />
          </button>
          {isUserLoggedIn() && (
            <button
              className="add-favorite-button2"
              onClick={(e) => {
                e.preventDefault();
                addToFavorites(selectedLocation.lat, selectedLocation.lon);
              }}
            >
              <img src="/icons/like.png" alt="favorite icon" className="icon" />
            </button>
          )}
        </div>
      )}
      {modalMessage && <Modal message={modalMessage} onClose={closeModal} />}
    </div>
  );
}

export default SearchBar;
