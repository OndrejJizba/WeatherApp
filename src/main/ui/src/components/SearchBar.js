import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./SearchBar.css";

function SearchBar() {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
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

  const handleCardClick = () => {
    if (selectedLocation) {
      navigate(`/forecast/${selectedLocation.lat}/${selectedLocation.lon}`);
    }
  };

  return (
    <div className="search-bar-container">
      <input
        type="text"
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        placeholder="Enter city name..."
      />
      <button onClick={handleSearch} disabled={loading}>
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
        <div className="selected-location" onClick={handleCardClick}>
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
          <p className="forecast-link">Click for forecast</p>
        </div>
      )}
    </div>
  );
}

export default SearchBar;
