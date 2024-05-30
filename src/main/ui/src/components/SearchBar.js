import React, { useState } from "react";
import axios from "axios";
import "./SearchBar.css";

function SearchBar() {
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    setSearchResults([]);
    setError(null);
    setLoading(true);
    try {
      const response = await axios.get(`/search?name=${searchTerm}`);
      if (response.data.length > 0) {
        setSearchResults(response.data);
        setSelectedLocation(null);
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
        <div className="selected-location">
          <h2>
            {selectedLocation.name}, {selectedLocation.country}
          </h2>
          <p>{selectedLocation.weather.temperature}°C</p>
          <p>{selectedLocation.weather.description}</p>
          <p>Sunrise: {selectedLocation.weather.sunrise}</p>
          <p>Sunset: {selectedLocation.weather.sunset}</p>
        </div>
      )}
    </div>
  );
}

export default SearchBar;
