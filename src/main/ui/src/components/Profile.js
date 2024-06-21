import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Profile.css";
import Modal from "./Modal";

const Profile = () => {
  const [username, setUsername] = useState("");
  const [favorites, setFavorites] = useState([]);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [modalMessage, setModalMessage] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      setError("Please login to view profile.");
      return;
    }
    const fetchProfileData = async () => {
      try {
        setIsLoading(true);
        const response = await axios.get("/profile", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUsername(response.data.username);
        setFavorites(
          response.data.map((city) => ({
            name: city.name,
            temp: Math.round(city.temp),
            icon: city.icon,
            id: city.id,
          }))
        );
        setIsLoading(false);
      } catch (err) {
        setError("Error fetching profile data");
        setIsLoading(false);
      }
    };

    fetchProfileData();
  }, []);

  const closeModal = () => {
    setModalMessage("");
  };

  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  const handleDeleteFavorite = async (cityId) => {
    const originalFavorites = [...favorites];
    const optimisticFavorites = favorites.filter((favCity) => favCity.id !== cityId);
    setFavorites(optimisticFavorites);
  
    try {
      const token = localStorage.getItem("token");
      const response = await axios.delete(`/favorites/${cityId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
  
      setModalMessage(response.data.message);
    } catch (err) {
      setFavorites(originalFavorites);
      setError("Error removing city from favorites");
    }
  };

  return (
    <div className="profile-container">
      {username && <p>Welcome, {username}!</p>}
      <h3>Your Favorite Cities</h3>
      {favorites.length === 0 ? (
        <p>No favorite cities added yet.</p>
      ) : (
        <div className="favorite-table">
          {favorites.map((city, index) => (
            <div key={city.id} className="favorite-row">
              <div className="favorite-cell">{city.name}</div>
              <div className="favorite-cell">{city.temp}°C</div>
              <div className="favorite-cell icon-cell">
                <img src={`${city.icon}`} alt="Weather icon" />
              </div>
              <button
              className="remove-favorite-button"
              onClick={() => handleDeleteFavorite(city.id)}
            >
              Remove
            </button>
            </div>
          ))}
        </div>
      )}
      {modalMessage && <Modal message={modalMessage} onClose={closeModal} />}
    </div>
  );
};

export default Profile;
