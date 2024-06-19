import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Profile.css";

const Profile = () => {
  const [username, setUsername] = useState("");
  const [favorites, setFavorites] = useState([]);
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(true);

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

  if (isLoading) {
    return <div className="loading">Loading...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="profile-container">
      {username && <p>Welcome, {username}!</p>}
      <h3>Your Favorite Cities</h3>
      {favorites.length === 0 ? (
        <p>No favorite cities added yet.</p>
      ) : (
        <div className="favorite-table">
          {favorites.map((city, index) => (
            <div key={index} className="favorite-row">
              <div className="favorite-cell">{city.name}</div>
              <div className="favorite-cell">{city.temp}°C</div>
              <div className="favorite-cell icon-cell">
                <img src={`${city.icon}`} alt="Weather icon" />
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Profile;
